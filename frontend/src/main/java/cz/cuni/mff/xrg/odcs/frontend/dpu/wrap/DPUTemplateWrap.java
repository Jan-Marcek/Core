package cz.cuni.mff.xrg.odcs.frontend.dpu.wrap;

import java.util.Locale;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;

/**
 * Wrap {@link DPUTemplateRecord} to made work with configuration and
 * configuration dialog easier.
 *
 * @author Petyr
 */
public class DPUTemplateWrap extends DPURecordWrap {

    /**
     * Wrapped DPUTemplateRecord.
     */
    private final DPUTemplateRecord dpuTemplate;

    /**
     * Create wrap for DPUTemplateRecord.
     *
     * @param dpuTemplate
     */
    public DPUTemplateWrap(DPUTemplateRecord dpuTemplate, Locale locale) {
        super(dpuTemplate, true, locale);
        this.dpuTemplate = dpuTemplate;
    }

    /**
     * Get DPU template record.
     *
     * @return DPU template record
     */
    public DPUTemplateRecord getDPUTemplateRecord() {
        return dpuTemplate;
    }

}
