package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserRowMapper;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

	private final UserDbStorage userStorage;
	private final JdbcTemplate jdbcTemplate;

	@Test
	public void testGetUserById() {
		User user1 = new User(1, null, null, null, null);
		user1.setName("Test User 1");
		user1.setLogin("testUser1");
		user1.setEmail("testuser1@example.com");
		user1.setBirthday(LocalDate.of(2000, 1, 1));
		userStorage.create(user1);

		Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
				);
	}

	@Test
	public void testCreateUser() {
		// Step 1: create a new user with test data
		User user = new User(1, null, null, null, null);
		user.setName("Test User");
		user.setLogin("testUser");
		user.setEmail("testuser@example.com");
		user.setBirthday(LocalDate.of(2000, 1, 1));

		// Step 2: call the create method on the user storage object
		User createdUser = userStorage.create(user);

		// Step 3: verify that the created user has the correct ID
		assertThat(createdUser.getId()).isPositive();

		// Step 4: verify that the user was actually created in the database
		String sql = "SELECT * FROM users WHERE id = ?";
		User retrievedUser = jdbcTemplate.queryForObject(sql, new Object[]{createdUser.getId()}, new UserRowMapper());

		assertThat(retrievedUser).isNotNull();
		assertThat(retrievedUser.getName()).isEqualTo(user.getName());
		assertThat(retrievedUser.getLogin()).isEqualTo(user.getLogin());
		assertThat(retrievedUser.getEmail()).isEqualTo(user.getEmail());
		assertThat(retrievedUser.getBirthday()).isEqualTo(user.getBirthday());
	}

	@Test
	public void testGetAllUsers() {
		// Step 1: add some test users to the database
		User user1 = new User(1, null, null, null, null);
		user1.setName("Test User 1");
		user1.setLogin("testuser1");
		user1.setEmail("testuser1@example.com");
		user1.setBirthday(LocalDate.of(2000, 1, 1));
		userStorage.create(user1);

		User user2 = new User(2, null, null, null, null);
		user2.setName("Test User 2");
		user2.setLogin("testuser2");
		user2.setEmail("testuser2@example.com");
		user2.setBirthday(LocalDate.of(2000, 2, 2));
		userStorage.create(user2);

		// Step 2: call the getAllUsers method on the user storage object
		List<User> allUsers = userStorage.getAllUsers();

		// Step 3: verify that the returned map contains all added users
		 assertThat(allUsers).containsExactlyInAnyOrder(user1, user2);
		//assertThat(allUsers).containsOnly(user1, user2);
	}

	@Test
	public void testUpdateUser() {
		// Step 1: add a test user to the database
		User user1 = new User(1, null, null, null, null);
		user1.setName("Test User 1");
		user1.setLogin("testUser1");
		user1.setEmail("testUser1@example.com");
		user1.setBirthday(LocalDate.of(2000, 1, 1));
		userStorage.create(user1);

		// Step 2: update the user's information
		user1.setName("UpdatedTestUser1");
		user1.setLogin("updatedTestUser1");
		user1.setEmail("updatedTestUser1@example.com");
		user1.setBirthday(LocalDate.of(2000, 2, 2));
		userStorage.update(user1);

		// Step 3: retrieve the updated user from the database
		User updatedUser = userStorage.getUserById(1);

		// Step 4: verify that the user's information has been updated
		assertThat(updatedUser)
				.isEqualTo(user1)
				.hasFieldOrPropertyWithValue("name", "UpdatedTestUser1")
				.hasFieldOrPropertyWithValue("login", "updatedTestUser1")
				.hasFieldOrPropertyWithValue("email", "updatedTestUser1@example.com")
				.hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 2, 2));
	}

	@Test
	public void testIsContainId() {
		User user = new User(1, "Test User", "testUser", "test@example.com", LocalDate.of(2000, 1, 1));
		userStorage.create(user);
		boolean result = userStorage.isContainId(user.getId());
		assertThat(result).isTrue();
	}


	@Test
	void contextLoads() {
	}
}
