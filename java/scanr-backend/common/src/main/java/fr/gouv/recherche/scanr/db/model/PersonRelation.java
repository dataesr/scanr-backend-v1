/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="v2.PersonRelation", description="Structure (leaders), Project or Publication relation to Person")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonRelation {

	@ApiModelProperty("Light fields in Full objects, else only id")
	private Person person; // et pas en plus structure dans le cas de Structure.leaders comme avant
	// relation fields :
	@ApiModelProperty("Role of the person in the object that is source of the relation")
	private String role;
	@ApiModelProperty("Only in a Structure")
	private String fromDate;
	// values of person fields as provided in the object that is source of the relation :
    private String firstName;
    private String lastName;
    @ApiModelProperty("Only in a Structure")
    private String title;
    @ApiModelProperty("Only in a Project")
    private String email;
    @ApiModelProperty("Only in a Publication")
    private String fullName;
    // type (et email dans FullStructure) supprimés, car venant de LightPerson si existant
	
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
}
