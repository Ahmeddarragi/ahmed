package org.sid.service;

import org.sid.entity.Userr;

public interface AuthService {
    String register(Userr user);
    String login(String email, String password);
   
}