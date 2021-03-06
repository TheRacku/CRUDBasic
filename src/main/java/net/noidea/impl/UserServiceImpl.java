package net.noidea.impl;

import net.noidea.model.User;
import net.noidea.repository.UserRepository;
import net.noidea.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void addUser(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> delete(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
            return userOptional;
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> update(User user) {
        Optional<User> userOptional = userRepository.findById(user.getId());
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            userRepository.delete(existingUser);
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            existingUser.setUsername(user.getUsername());
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setAge(user.getAge());
            existingUser.setCountry(user.getCountry());
            userRepository.save(existingUser);
            return Optional.of(existingUser);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findByCriteria(String criteria, String searchItem) {
        switch (criteria.toLowerCase()) {
            case "username":
                return this.userRepository.findByUsername(searchItem);
            case "firstname":
                return this.userRepository.findByFirstName(searchItem);
            case "lastname":
                return this.userRepository.findByLastName(searchItem);
            case "age":
                try {
                    Integer age = Integer.valueOf(searchItem);
                    return this.userRepository.findByAge(age);
                } catch (NumberFormatException e) {
                    System.out.println("Could not convert age to number ...");
                }
                return new ArrayList<>();
            case "country":
                return this.userRepository.findByCountry(searchItem);
        }
        return new ArrayList<>();
    }
}
