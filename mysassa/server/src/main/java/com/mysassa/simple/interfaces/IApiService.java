package com.mysassa.simple.interfaces;

/**
 * This interface means that there is @ApiCall annotations, and that this service is Api Call accessible.
 *
 * It is used by the Module System to detect if it should parse for the metadata, it comes with a reflection
 * cost.
 *
 * I encourage using it only on classes dedicated for it.
 *
 * It extends ISimple service as a convenience, as it serves as that, and they both do nothing but act as a label.
 */
public interface IApiService {}
