package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;

import java.util.Date;
import java.util.List;

@ApiModel(value="v2.Patent", description="in Publication")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Patent {

    private String id;
    private Boolean isPriority;
    private String ipType;
    private String office;
    private Date applicationDate;
    private String applicationNumber;
    private String internatApplicationNumber;
    private String regionalApplicationNumber;
    private Date publicationDate;
    private String publicationNumber;
    private Date grantedDate;
    private List<Link> links;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIsPriority() {
        return isPriority;
    }

    public void setIsPriority(Boolean priority) {
        isPriority = priority;
    }

    public String getIpType() {
        return ipType;
    }

    public void setIpType(String ipType) {
        this.ipType = ipType;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getInternatApplicationNumber() {
        return internatApplicationNumber;
    }

    public void setInternatApplicationNumber(String internatApplicationNumber) {
        this.internatApplicationNumber = internatApplicationNumber;
    }

    public String getRegionalApplicationNumber() {
        return regionalApplicationNumber;
    }

    public void setRegionalApplicationNumber(String regionalApplicationNumber) {
        this.regionalApplicationNumber = regionalApplicationNumber;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getPublicationNumber() {
        return publicationNumber;
    }

    public void setPublicationNumber(String publicationNumber) {
        this.publicationNumber = publicationNumber;
    }

    public Date getGrantedDate() {
        return grantedDate;
    }

    public void setGrantedDate(Date grantedDate) {
        this.grantedDate = grantedDate;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
