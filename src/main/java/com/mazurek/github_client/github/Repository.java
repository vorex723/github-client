package com.mazurek.github_client.github;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Repository {
    private String name;
    private String username;
    private boolean fork;

    @JsonProperty("owner")
    private void getRepositoryOwnerName(Map<String, String> properties){
        this.username = properties.get("login");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repository that = (Repository) o;
        return fork == that.fork && Objects.equals(name, that.name) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, username, fork);
    }
}
