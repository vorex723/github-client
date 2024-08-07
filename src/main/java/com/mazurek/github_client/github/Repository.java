package com.mazurek.github_client.github;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Repository {
    private String name;
    private String username;
    private List<Branch> branches;
    private boolean fork;
}
