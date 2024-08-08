package com.mazurek.github_client.github.dto;

import com.mazurek.github_client.github.Branch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchDto {
    private String name;
    private String sha;

    public BranchDto(Branch branch) {
        this.name = branch.getName();
        this.sha = branch.getSha();
    }
}
