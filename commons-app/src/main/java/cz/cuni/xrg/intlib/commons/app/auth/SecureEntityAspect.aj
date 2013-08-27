package cz.cuni.xrg.intlib.commons.app.auth;


import javax.persistence.Transient;

public aspect SecureEntityAspect {

	declare parents : cz.cuni.xrg.intlib.commons.app.* implements SecureEntity;

	@Transient
	private transient boolean SecureEntity.deletable = false;

	public boolean SecureEntity.isDeletable() {
		return deletable;
	}

	public void SecureEntity.setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

}