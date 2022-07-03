package com.example.demo.model.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
// add quote to the table name to avoid key word conflict for Spring 2.7
// according to https://stackoverflow.com/questions/72546596/tests-failing-when-upgrading-to-spring-boot-2-7-commandacceptanceexception-e

@Table(name="\"user\"")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty
	@Getter
	@Setter
	private long id;

	@Column(nullable = false, unique = true)
	@JsonProperty
	@Getter
	@Setter
	private String username;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(nullable = false)
	@Getter
	@Setter
	private String password;

	public User() {
	}

	public User(long id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "cart_id", referencedColumnName = "id")
	@JsonIgnore
	private Cart cart;

	public User(long id, String username, String password, Cart cart) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.cart = cart;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

}
