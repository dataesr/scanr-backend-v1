/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import io.swagger.annotations.ApiModel;

/**
 * Kind of a structure:
 * <ul>
 *     <li>COMPANY: private or public company</li>
 *     <li>RNSR: strcuture from RNSR</li>
 * </ul>
 * @deprecated L'enum est à supprimer, passage à un type String pour Kind
 */
@ApiModel("v2.StructureKind")
@Deprecated
public enum StructureKind {
    COMPANY, RNSR
}
