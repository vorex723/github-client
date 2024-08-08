package com.mazurek.github_client.github.dto;

import com.mazurek.github_client.github.Branch;
import com.mazurek.github_client.github.Repository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRepositoryDto {
    private String username;
    private String name;
    private List<BranchDto> branches;

    public UserRepositoryDto(Repository repository, List<Branch> branches){
        this.username = repository.getUsername();
        this.name = repository.getName();
        this.branches = new ArrayList<>();
        branches.forEach(branch -> this.branches.add(new BranchDto(branch)));
    }
}
