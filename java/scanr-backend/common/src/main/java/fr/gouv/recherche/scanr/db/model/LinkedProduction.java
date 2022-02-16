package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import fr.gouv.recherche.scanr.db.model.publication.PublicationAuthorRelation;
import io.swagger.annotations.ApiModel;

import java.util.Date;
import java.util.List;

@ApiModel(value="v2.LinkedProduction", description="in Publication")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LinkedProduction {

    private String typeCitation;
    private String productionType;
    private Publication production;
    private String title;
    private Date publicationDate;
    private List<PublicationAuthorRelation> authors;
    private String source;
    private String doi;
    private String link;

    public String getTypeCitation() {
        return typeCitation;
    }

    public void setTypeCitation(String typeCitation) {
        this.typeCitation = typeCitation;
    }

    public String getProductionType() {
        return productionType;
    }

    public void setProductionType(String productionType) {
        this.productionType = productionType;
    }

    public Publication getProduction() {
        return production;
    }

    public void setProduction(Publication production) {
        this.production = production;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public List<PublicationAuthorRelation> getAuthors() {
        return authors;
    }

    public void setAuthors(List<PublicationAuthorRelation> authors) {
        this.authors = authors;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
