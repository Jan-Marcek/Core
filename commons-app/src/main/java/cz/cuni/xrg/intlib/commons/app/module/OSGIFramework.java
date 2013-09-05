package cz.cuni.xrg.intlib.commons.app.module;

import java.io.FileNotFoundException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrap OSGI framework functionality.
 * 
 * @author Petyr
 * 
 */
class OSGIFramework {

	/**
	 * OSGi framework class.
	 */
	private org.osgi.framework.launch.Framework framework = null;

	/**
	 * OSGi context.
	 */
	private org.osgi.framework.BundleContext context = null;

	/**
	 * Store loaded bundles. Bundles are store under their name.
	 */
	private java.util.Map<String, BundleContainer> loadedBundles = new java.util.HashMap<>();

	/**
	 * Logger class.
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(OSGIFramework.class);

	/**
	 * Return configuration used to start up OSGi implementation.
	 * 
	 * @param exportedPackages names of additional packages to export started
	 *            and separated by comma
	 * @return
	 */
	private java.util.Map<String, String> prepareSettings(String exportedPackages) {
		java.util.Map<String, String> config = new java.util.HashMap<>();
		config.put("osgi.console", "");
		config.put("osgi.clean", "true");
		config.put("osgi.noShutdown", "true");
		// export packages
		config.put(org.osgi.framework.Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
				exportedPackages);
		return config;
	}

	/**
	 * Start OSGi framework.
	 * 
	 * @param exportedPackages names of additional packages to export started
	 *            and separated by comma
	 * @throws FrameworkStartFailedException
	 */
	public void start(String exportedPackages)
			throws FrameworkStartFailedException {
		FrameworkFactory frameworkFactory = null; // org.eclipse.osgi.launch.EquinoxFactory

		try {
			frameworkFactory = java.util.ServiceLoader
					.load(FrameworkFactory.class).iterator().next();
		} catch (Exception ex) {
			LOG.error("Failed to load osgi framework class.", ex);
			// failed to load FrameworkFactory class
			throw new FrameworkStartFailedException(
					"Can't load class FrameworkFactory.", ex);
		}

		framework = frameworkFactory
				.newFramework(prepareSettings(exportedPackages));
		context = null;
		try {
			// start OSGi container ..
			framework.start();
		} catch (org.osgi.framework.BundleException ex) {
			LOG.error("Failed to lstart framework.", ex);
			// failed to start/initiate framework
			throw new FrameworkStartFailedException(
					"Failed to start OSGi framework.", ex);
		}

		try {
			context = framework.getBundleContext();
		} catch (SecurityException ex) {
			LOG.error("Failed to get osgi context.", ex);
			// we have to stop framework ..
			stop();
			throw new FrameworkStartFailedException(
					"Failed to get OSGi context.", ex);
		}
	}

	/**
	 * Stop OSGi framework. Does not uninstall installed bundles.
	 */
	public void stop() {
		// TODO: Petyr uninstall all bundles
		for (BundleContainer bundle : loadedBundles.values()) {
			try {
				bundle.uninstall();
			} catch (BundleException e) {
				LOG.error("Failed to uninstall bundle {}",
						bundle.getUri(), e);
				}
		}
		loadedBundles.clear();
		try {
			if (framework != null) {
				// stop equinox
				framework.stop();
			}
		} catch (Exception e) {
			// we can't throw here ..
		}
		framework = null;
		context = null;
	}

	/**
	 * Return bundle if loaded, otherwise return null. 
	 * @param uri Uri to install bundle from.
	 * @return BundleContainer for given URL or null.
	 */
	public BundleContainer getBundle(String uri) {
		// has bundle been already loaded?
		if (loadedBundles.containsKey(uri)) {
			return loadedBundles.get(uri);
		}
		return null;
	}
	
	/**
	 * Install bundle into framework.
	 * 
	 * @param uri Uri to install bundle from.
	 * @return BundleContainer or null.
	 * @throws BundleInstallFailedException
	 */
	public BundleContainer installBundle(String uri)
			throws BundleInstallFailedException {
		// has bundle been already loaded?
		if (loadedBundles.containsKey(uri)) {
			return loadedBundles.get(uri);
		}

		// load bundle
		BundleContainer bundleContainer = null;
		try {
			Bundle bundle = context.installBundle(uri);
			bundleContainer = new BundleContainer(bundle, uri);
			// add bundle to the loaded bundle list and reverse list
			loadedBundles.put(uri, bundleContainer);
		} catch (org.osgi.framework.BundleException e) {
			// uninstall bundle
			uninstallBundle(bundleContainer);
			// ..
			throw new BundleInstallFailedException(e);
		}
		return bundleContainer;
	}

	/**
	 * Uninstall bundle from framework.
	 * 
	 * @param bundle Bundle to uninstall.
	 * @return False if exception was thrown during uninstalling.
	 */
	public boolean uninstallBundle(BundleContainer bundleContainer) {
		try {
			if (bundleContainer == null) {
				return true;
			} else {
				bundleContainer.uninstall();
				// remove record about bundle
				String key = bundleContainer.getUri();
				// remove records related to bundle
				loadedBundles.remove(key);
			}
		} catch (org.osgi.framework.BundleException e) {
			LOG.error("Failed to uninstall bundle {}",
					bundleContainer.getUri(), e);
			return false;
		}
		return true;
	}

	/**
	 * Load class "module.${Bundle-Name}" from bundle on given uri.
	 * 
	 * @param uri Uri to bundle.
	 * @return Loaded object class.
	 * @throws FileNotFoundException
	 * @throws BundleInstallFailedException
	 * @throws ClassLoadFailedException
	 */
	public Object loadClass(String uri)
			throws BundleInstallFailedException,
				FileNotFoundException,
				ClassLoadFailedException {
		// load bundle
		BundleContainer bundleContainer = installBundle(uri);
		// get location of bundle main class
		String packageName = (String) bundleContainer.getBundle().getHeaders()
				.get("DPU-Package");
		if (packageName == null) {
			LOG.error("'DPU-Package' undefined for '{}'", uri);
			throw new ClassLoadFailedException("Can't find 'DPU-Package' record in bundle's manifest.mf");
		}
		String className = (String) bundleContainer.getBundle().getHeaders()
				.get("DPU-MainClass");
		if (className == null) {
			LOG.error("'DPU-MainClass' undefined for '{}'", uri);
			throw new ClassLoadFailedException("Can't find 'DPU-MainClass' record in bundle's manifest.mf");
		}
		String fullMainClassName = packageName + "." + className;
		// load object
		Object loadedObject;
		// can throw ClassLoadFailedException
		loadedObject = bundleContainer.loadClass(fullMainClassName);
		
		return loadedObject;
	}

}
