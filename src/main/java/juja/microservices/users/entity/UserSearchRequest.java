package juja.microservices.users.entity;


import lombok.Data;
import org.hibernate.validator.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 */
@Data
public class UserSearchRequest {
    @Email
    @Size(min = 3, message = "Too short for email address")
    public String email;

    @Size(max = 36, min = 36, message = "UUID must consist total of 36 characters with hyphens")
    @Pattern(regexp="[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", message="UUID must fit pattern 8-4-4-4-12")
    public String uuid;

    @Pattern(regexp="^(?=\\s*\\S).*$")
    public String slack;

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        if (email != null)
            map.put("email", email);
        if (uuid != null)
            map.put("uuid", uuid);
        if (slack != null)
            map.put("slack", slack);
        return map;
    }

}
