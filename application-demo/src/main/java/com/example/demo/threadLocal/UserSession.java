package com.example.demo.threadLocal;

import lombok.Data;

/**
 * @author markchou
 * @createtime 2025/12/25
 */
@Data
public class UserSession {

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

    public UserSession(String id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    private String id;
        private String userName;
}
