package com.s2tv.sportshop.service;

import com.s2tv.sportshop.model.Users;
import com.s2tv.sportshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Users getUserById(String userId) {
        Optional<Users> user = userRepository.findById(userId);
        return user.orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public String changePassword(String email, String oldPassword, String newPassword) {
        Optional<Users> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "User not found";
        }

        Users user = userOpt.get();
        // Add password verification logic here with BCrypt if needed.
        user.setPassword(newPassword); // Set the new password after hashing
        userRepository.save(user);
        return "Password changed successfully";
    }

    public Users updateUser(String userId, Users userUpdateData) {
        Users user = getUserById(userId);
        user.setFullName(userUpdateData.getFullName());
        user.setPhone(userUpdateData.getPhone());
        user.setEmail(userUpdateData.getEmail());

        return userRepository.save(user);
    }

//    public Users addAddress(String userId, Address addressData) {
//        Users user = getUserById(userId);
//        if (addressData.getIsDefault()) {
//            user.getAddresses().forEach(address -> address.setIsDefault(false));
//        }
//        user.getAddresses().add(addressData);
//        return userRepository.save(user);
//    }
//
//    public Users updateAddress(String userId, int index, Address updateData) {
//        Users user = getUserById(userId);
//        List<Address> addresses = user.getAddresses();
//        if (index < 0 || index >= addresses.size()) {
//            throw new RuntimeException("Address not found");
//        }
//
//        Address address = addresses.get(index);
//        address.setName(updateData.getName());
//        address.setPhone(updateData.getPhone());
//        address.setHomeAddress(updateData.getHomeAddress());
//        address.setProvince(updateData.getProvince());
//        address.setDistrict(updateData.getDistrict());
//        address.setWard(updateData.getWard());
//        address.setDefault(updateData.isDefault());
//
//        if (updateData.isDefault()) {
//            for (int i = 0; i < addresses.size(); i++) {
//                if (i != index) addresses.get(i).setDefault(false);
//            }
//        }
//        return userRepository.save(user);
//    }
//
//    public String deleteAddress(String userId, int index) {
//        User user = getUserById(userId);
//        List<Address> addresses = user.getAddresses();
//        if (index < 0 || index >= addresses.size()) {
//            return "Address not found";
//        }
//
//        addresses.remove(index);
//        userRepository.save(user);
//        return "Deleted address successfully";
//    }
}
