/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.model;

import fr.gouv.recherche.scanr.db.model.I18nValue;
import org.springframework.data.annotation.Id;

import java.text.SimpleDateFormat;
import java.util.List;

@Deprecated
public class FullStructureIndex {
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final String INDEX = "scanr";
    public static final String TYPE = "fullstructure";


    public static final class FIELDS {
        public static final String ID = "id";
        public static final String LABEL = "label";
        public static final String ALIAS = "alias";
        public static final String KIND = "kind";
        public static final String PUBLIC_ENTITY = "publicEntity";
        public static final String LOGO = "logo";
        public static final String ACRONYM = "acronym";
        public static final String COMPANY_TYPE = "companyType";
        public static final String NATURE = "nature";
        public static final String LEVEL = "level";
        public static final String CREATION_YEAR = "creationYear";
        public static final String BADGES = "badges";
        public static final String ACTIVITY_LABELS = "activityLabels";
        public static final String RAW = "raw";
        public static final String MAIN_WEBSITE = "mainWebsite";


        public static final class ADDRESS {
            public static final String PREFIX = "address.";
            public static final String POSTCODE = PREFIX + "postcode";
            public static final String URBAN_UNIT = PREFIX + "urbanUnit";
            public static final String DEPARTEMENT = PREFIX + "departement";
            public static final String CITY = PREFIX + "city";
            public static final String GPS = PREFIX + "gps";
        }

        public static final class TYPE {
            public static final String ALL = "type";
            public static final String PREFIX = ALL + ".";
            public static final String CODE = PREFIX + "code";
            public static final String LABEL = PREFIX + "label";
        }


        public static final class LEADERS {
            public static final String PREFIX = "leaders.";
            public static final String COMPLETE_NAME = PREFIX + "completeName";
            public static final String TITLE = PREFIX + "title";
        }

        public static final class NAF {
            public static final String PREFIX = "naf.";
            public static final String CODE = PREFIX + "code";
        }

        public static final class DOMAINE {
            public static final String PREFIX = "domaine.";
            public static final String CODE = PREFIX + "code";
        }

        public static final class ERC {
            public static final String PREFIX = "erc.";
            public static final String CODE = PREFIX + "code";
        }

        public static final class INSTITUTIONS {
            public static final String PREFIX = "institutions.";
            public static final String ID = PREFIX + "id";
            public static final String CODE = PREFIX + "code";
            public static final String LABEL = PREFIX + "label";
            public static final String ACRONYM = PREFIX + "acronym";
        }

        public static final class PROJECTS {
            public static final String PREFIX = "projects.";
            public static final String ID = PREFIX + "id";
            public static final String ACRONYM = PREFIX + "acronym";
            public static final String LABEL = PREFIX + "label";
            public static final String DESCRIPTION = PREFIX + "description";
            public static final String CALL = PREFIX + "call";
            public static final String CALL_LABEL = PREFIX + "callLabel";
        }

        public static final class PUBLICATIONS {
            public static final String PREFIX = "publications.";
            public static final String TITLE = PREFIX + "title";
            public static final String SUBTITLE = PREFIX + "subtitle";
            public static final String AUTHORS = PREFIX + "authors";
            public static final String SUMMARY = PREFIX + "summary";
            public static final String ALTERNATIVE_SUMMARY = PREFIX + "alternativeSummary";
        }

        public static final class WEBSITE {
            public static final String PREFIX = "websiteContents.";
            public static final String BASE_URL = PREFIX + "baseURL";

            public static final class WEBPAGES {
                public static final String PREFIX = WEBSITE.PREFIX + "webPages.";
                public static final String CONTENT = PREFIX + "content";
            }
        }
    }

    @Id
    private String id;

    private I18nValue label;

    private List<String> alias;

    private String kind;

    private boolean publicEntity;

    private String logo;

    private I18nValue acronym;

    private String nature;

    private String level;

    private Integer creationYear;

    /**
     * Badge codes
     */
    private List<String> badges;

    private List<I18nValue> activityLabels;

    private String mainWebsite;

    public FullStructureIndex() {
    }

    public FullStructureIndex(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nValue getLabel() {
        return label;
    }

    public void setLabel(I18nValue label) {
        this.label = label;
    }

    public List<String> getAlias() {
        return alias;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public boolean isPublicEntity() {
        return publicEntity;
    }

    public void setPublicEntity(boolean publicEntity) {
        this.publicEntity = publicEntity;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public I18nValue getAcronym() {
        return acronym;
    }

    public void setAcronym(I18nValue acronym) {
        this.acronym = acronym;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getCreationYear() {
        return creationYear;
    }

    public void setCreationYear(Integer creationYear) {
        this.creationYear = creationYear;
    }

    public List<String> getBadges() {
        return badges;
    }

    public void setBadges(List<String> badges) {
        this.badges = badges;
    }

    public void setActivityLabels(List<I18nValue> activityLabels) {
        this.activityLabels = activityLabels;
    }

    public List<I18nValue> getActivityLabels() {
        return activityLabels;
    }

    public String getMainWebsite() {
        return mainWebsite;
    }

    public void setMainWebsite(String mainWebsite) {
        this.mainWebsite = mainWebsite;
    }
}
