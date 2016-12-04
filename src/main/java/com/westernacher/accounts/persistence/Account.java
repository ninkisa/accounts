package com.westernacher.accounts.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity(name = "Account")
@Table(name = "ACCOUNT")
@NamedQueries({ 
	@NamedQuery(name = "Account.getAll", query = "SELECT a FROM Account a"),
})
@XmlRootElement(name = "Account")
public class Account implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private int id;
	
	@Column(name = "FIRST_NAME", nullable = false)
	private String firstName;
	
	@Column(name = "LAST_NAME", nullable = false)
	private String lastName;
	
	@Column(name = "EMAIL", nullable = false)
	private String email;
	
	@Column(name = "BIRTH_DATE", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date birthDate;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public int getId() {
		return id;
	}
	
}
