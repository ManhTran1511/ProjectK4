package com.fpt.edu.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(	name = "user")
public class User {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 20)
	private String username;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	@NotBlank
	@Size(max = 120)
	private String password;

	@NotBlank
	@Size(max = 120)
	private String image;


	@ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinTable(
			name="users_roles",
			joinColumns={@JoinColumn(name="user_id", referencedColumnName="ID")},
			inverseJoinColumns={@JoinColumn(name="role_id", referencedColumnName="ID")})
	private List<Role> roles = new ArrayList<>();
//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(
//			name="users_roles",
//			joinColumns={@JoinColumn(name="user_id", referencedColumnName="ID")},
//			inverseJoinColumns={@JoinColumn(name="role_id", referencedColumnName="ID")})
//	private Set<Role> roles = new HashSet<>();

	public User() {
	}

	public User(Long id, String username, String email, String password, String image, List<Role> roles) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.image = image;
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
//	public Set<Role> getRoles() {
//		return roles;
//	}
//
//	public void setRoles(Set<Role> roles) {
//		this.roles = roles;
//	}
}
