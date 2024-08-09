
# GitHub Client

Small app for gathering informations about user public repositories. For communication uses Github REST API V3. 




## API Reference

#### Get user repositories

```http
  GET /api/v1/github/users/${username}/repos
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `username`      | `string` | **Required**. Username of who repositories will be listed |

Rerturns list of user repositories which are not forks. Every repository contains list of branches with name and last commit sha.
## Tech Stack

**Server:** Spring Boot 3.3.2, Java 21


## Run Locally

Clone the project

```bash
  git clone https://github.com/vorex723/github-client.git
```

Go to the project resources directory

```bash
  github-client/src/main/resources
```

Create file "application.properties" with those keys in it. Github token can be generated on their page by following path:

Settings >> Developer settings >> Personal access tokens >> Fine-grained tokens >> Generate new token

```bash
  spring.application.name=github-client
  github.api.baseUrl=https://api.github.com
  github.token={YOUR_GITHUB_TOKEN}
 
```


App should be ready to go.
