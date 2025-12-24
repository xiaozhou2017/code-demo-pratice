package entitiy;

import java.io.Serializable;

/**
 * @author markchou
 * @createtime 2025/12/24
 */

public class TestUser implements Serializable {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private Long id;
    private String name;
    private String email;
}
