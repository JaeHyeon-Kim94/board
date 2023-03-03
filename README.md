# **[board]**

## Concept  
<br/>
주제별 게시판 매니저 관리, 사용자 권한 관리, 리소스 접근 권한 동적 제어 등의 기능이 구현된 게시판 관리 성격의 어플리케이션입니다.    

많이 부족하더라도 좋게 봐주시면 감사하겠습니다.

<br/>

### 기술 스택
 - DataBase: MySQL, H2(test)
 - Backend : Java11, Springboot, Spring Security, MyBatis
 - Frontend: thymeleaf
 - etc : Lombok, Swagger, OAuth2.0
<br/><br/>


### ERD

![ERD](https://user-images.githubusercontent.com/84181831/222631409-f8a3706d-b9b3-4239-8e92-4ee30497e383.png)






## 구조 및 역할
---

 - **authorization-server**  
자체 인가 서버. 회원가입 및 폼 로그인 인증 정보를 토대로 client 서버에게 토큰을 발급합니다.  

<br>

 - **oauth2-authorization-client-server**  
소셜 로그인 처리 후 토큰 발급하는 인가 서버.   
client 서버로부터 oauth 기반 인증처리를 위임받아 서비스 제공자(Google, Naver)와의 통신, client 서버로의 토큰 발급을 담당합니다.

<br>

 
 - **client**  
상기한 두 서버로부터 받아온 토큰을 기반으로 게시판 관리 성격의 서비스를 제공합니다.  

<br>

> 인가서버와 client 서버를 분리한 이유는 확장성있는 아키텍쳐에 대한 고민에서 기인하였습니다.  
> client 서버에서는 JWT 기반으로 사용자를 인증 및 인가처리를 하게 되는데, 자가 수용적인 성격을 가지는 JWT는 세션에 비해 스케일 아웃에 유리합니다.  
> 
> 만약 client 서버에서 사용자 인증 및 인가에 더해 토큰에 대한 서명과 검증까지 책임지게 된다면 기존 서버가 한계에 도달했을 때 수평 스케일링이 까다로워질 수 있으며, JWT의 장점이 퇴색된다고 생각하였습니다.
> 
> 따라서 인가 서버에서 JWT을 서명과, 검증을 위한 JWK Set을 제공하는 역할을 수행하고 client 서버에서는 JWK Set uri를 통해 해당 토큰을 검증할 수 있도록 구성하였습니다.
 
<br/><br/>

## 주요 기능
---
<br/>

### 1. 회원 가입 및 로그인

<br>

  -  회원 가입과 폼 로그인

사용자는 인가 서버에서 제공하는 UI를 통해 회원가입, 폼 로그인을 할 수 있습니다.  
현재 SSL을 통한 암호화 통신이 이루이지지 않기 때문에, 회원가입 및 로그인 과정에서 전송되는 사용자의 패스워드는 서버에서 발급한 RSA 공개키로 암호화된 후 서버로 전송됩니다.  
회원가입시 전달받은 패스워드는 RSA 비밀키로 복호화 후 곧바로 Bcrypt 해시함수를 통해 암호화된 후 관리됩니다.
<br/>

 - 소셜 로그인

client 서버는 OAuth2 기반 소셜 로그인 처리를 oauth2-authorization-client-server에게 위임합니다. OAuth2의 권한 부여 타입중 Authorization Code Grant Type을 사용해 소셜 로그인 서비스 제공자로부터 토큰, 그리고 사용자 정보를 얻어와 인증처리를 하게 됩니다.  



<br/>


### 2. 사용자 인증 및 인가  

<br/>

client 서버는 두 인가 서버로부터 동일한 포맷의 JWT를 받아 인증과 인가처리를 하게 됩니다. 

```
{
  "sub": "~",
  "aud": "http://127.0.0.1:8080",
  "nbf": 1677392776,
  "iss": "http://127.0.0.1:9001",
  "exp": 1677393676,
  "iat": 1677392776,
  "nickname": "~",
  "authorities": [
    "ROLE_USER"
  ],
  "username": "~"
}

```

 - 인증

    - 검증  
client 서버에서는 두 인가 서버로부터 받은 토큰값을 cookie에 담아 인증처리에 사용합니다.  cookie에 담긴 토큰은 검증 객체에서 JWK Set uri를 통해 받아온 공개키를 토대로 검증됩니다. 

    - 인증처리  
  Spring Security에서는 인증 객체를 토대로 사용자의 인증 상태를 판단하며, 인증 객체는 컨텍스트에 저장되어 관리 및 참조됩니다. 

     - 갱신  
cookie life time은 JWT 만료시간보다 조금 더 길게 설정되어있습니다. 쿠키에 담긴 JWT가 만료되었을 시 refresh token을 DB로부터 조회해 인가서버로부터 재발급 후 쿠키 또한 갱신됩니다. 

<br/>

> 로컬 스토리지를 사용하게 되면 XSS 공격에 취약하기 때문에 cookie로 보관하는게 맞다고 생각합니다.  
> 쿠키 또한 CSRF 공격에 노출될 수 있지만, SSL 그리고 Cookie의 secure, samesite 속성을 이용하여 보완이 가능하다고 알고 있습니다.  
> 
> 더 안전하게 관리하기 위해서 refresh token을 cookie에 보관하고 access token은 Authorization 헤더를 이용하는 방법도 있으며 프런트 개발자와의 상호 협의가 필요합니다.

<br/>

 - 인가

JWT에는 registered claims외에 private claims(authorities, username)이 포함되어있습니다.  
JWT에 포함된 권한 정보는 이 인증 객체에 매핑하여 인가 과정에서 사용됩니다.

<br/>

인가처리를 위해선 `(1)보호되는 자원의 위치 정보`와 `(2)접근 권한 정보`, 그리고 `(3)접근을 요청한 사용자의 정보`가 필요합니다.  인가를 위해선 인증처리가 선행되며, 인증 객체에는 권한정보가 매핑되어 있으므로 서버에서는 (1), (2)에 해당하는 정보를 보관하고 관리할 필요가 있습니다.  
<br/>

**`(1) 보호되는 자원의 위치 정보  `**

[UrlFilterSecurityMetadataSource.java](https://github.com/JaeHyeon-Kim94/board/blob/master/client/src/main/java/io/oauth2/client/security/metadatasource/UrlFilterInvocationSecurityMetadataSource.java)
<details>
<summary>일부 소스</summary>

```java
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourceAuthorityMap;
    private final ResourceService resourceService;

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

        HttpServletRequest request = ((FilterInvocation) object).getRequest();

        if(resourceAuthorityMap != null){
            for (Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : resourceAuthorityMap.entrySet()) {
                RequestMatcher requestMatcher = entry.getKey();
                if(requestMatcher.matches(request)){
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    public void reload(){
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> reloadedMap = resourceService.getResourceList();
        resourceAuthorityMap.clear();
        resourceAuthorityMap.putAll(reloadedMap);
    }
}
```
</details>
<br/>
위에서 말한 1, 2에 대한 정보는 필요할 때마다 DB로부터 받아오기엔 비효율적이기 때문에 어플리케이션 구동시, 또는 권한 및 리소스에 대한 정보 변동시에만 받아와 Map에 보관해두도록 하였습니다.  

<br/>
LinkedHashMap을 사용한 데는 이유가 있습니다. RequestMatcher에 담기는 url path가 표현식이기 때문에 일반적인 값과  구체적인 값이 있습니다. 따라서 구체적인 값은 선순위로, 일반적인 값은 후순위로 배치해야 잘못된 자원이 매핑되는 불상사가 발생하지 않습니다.  

> ex)  
> /api/boards*/**
> 
> /api/boards/category/\*/subject/\*/management
>
> 예를 들어 위 두 표현식의 경우 첫 번째 표현식이 후순위에 배치되어야 자원에 대한 정확한 권한 정보를 반환할 것입니다.

따라서 해당 테이블을 설계할 때 정렬 기준이 될 컬럼이 포함되도록 할 필요가 있습니다.

<br/>

**`(2)  자원에 대한 접근 권한`**

위의 객체에선 결과적으로 자원에 대한 접근 권한이 무엇인지를 반환합니다. 사용자가 가진 권한 정보가 반환된 결과값과 같을 경우 인가처리는 성공하게 됩니다.  
그런데, 관리자 권한은 일반 사용자 권한보다 상위 권한으로서 일반 사용자가 접근할 수 있는 일반적인 리소스에 대해서 당연히 접근할 수 있어야 합니다. Spring Security에서는 기본적으로 권한정보가 **정확히 일치**하는 경우만을 따집니다.  
따라서 별다른 설정이 없다면 관리자는 자신의 하위 권한을 모두 가지고 있어야 하는데 그건 명확하지 않을 뿐더러 사용자별로 관리해야 할 권한 데이터가 크게 증가할 수 있기 때문에 권한을 계층구조로 구성할 필요가 있습니다. 

Spring Security에서는 이러한 문제를 해결하기 위한 객체(RoleHierarchy, RoleHierarchyVoter)를 제공하고 있어 설정을 통해 구현하였으며, 권한 정보를 저장하는 테이블 또한 자기참조관계를 맺도록 하였습니다.


<br/>


### 3. 리소스와 권한, 사용자

해당 어플리케이션에서는 관리자가 권한과 리소스에 대한 제어를 동적으로 할 수 있도록 API를 제공하고 있습니다.   
리소스, 권한에 대한 정보가 변동이 있을 시에는 reload되며 자동 반영됩니다.

<details>
<summary>Resource API</summary>

<br/>

 - 기본 정보

id  | 메서드  | 요청 URL                      | 응답                                | 설명
--- | ---     | ---                           | ---                                 | ---
1   | POST    | /api/resources                | 201(Location : /api/resources/{id}) |  리소스 등록
2   | PUT     | /api/resources/{id}           | 204                                 |  리소스 수정(id : resource 식별자)
3   | GET     | /api/resources/{id}           | 200                                 | 리소스 한 건 조회
4   | GET     | /api/resources                | 200                                 | 리소스 리스트 조회 (uri variable(optional) : offset=value, size=value)
5   | DELETE  | /api/resources/{id}           | 204                                 | 리소스 삭제

<br/><br/>


 - 파라미터



<table>
    <th>
        id
    </th>
    <th>
        요청 변수명
    </th>
    <th>
        필수 여부
    </th>
    <th>
        설명
    </th>
    <tr>
        <td rowspan="5">1, 2</td><td>type</td><td>Y</td><td>현재 url방식만 허용. must be 'url'</td>
    </tr>
    <tr>
        <td>level</td><td>Y</td><td>리소스의 구체적인 수준에 비례한 값. 일반적인 경로보다 높아야 합니다.</td>
    </tr>
    <tr>
        <td>value</td><td>Y</td><td>type에 따른 리소스 경로. ex) url type의 경우 : /api/boards*/**</td>
    </tr>
    <tr>
        <td>rold_id</td><td>Y</td><td>리소스 접근에 필요한 권한의 id.</td>
    </tr>
    <tr>
        <td>http_method</td><td>N</td><td>HttpMethod(GET, POST, DELETE, PUT) : null일시 모든 요청에 대해 매핑됩니다.</td>
    </tr>
</table>
</details>

---
<br/><br/>

<details>
<summary>Role API</summary>

<br/>

 - 기본 정보

id  | 메서드  | 요청 URL                      | 응답                                | 설명
--- | ---     | ---                           | ---                                 | ---
1   | PUT     | /api/roles/{id}           | 201(Location : /api/roles/{id}) or 204                                 |  권한 등록 및 수정(id : role 식별자)
2   | GET     | /api/roles/{id}           | 200                                 | 권한 한 건 조회
3   | GET     | /api/roles                | 200                                 | 권한 리스트 조회 (uri variable(optional) : offset=value, size=value)
4   | DELETE  | /api/roles/{id}           | 204                                 | 권한 삭제

<br/><br/>


 - 파라미터



<table>
    <th>
        id
    </th>
    <th>
        요청 변수명
    </th>
    <th>
        필수 여부
    </th>
    <th>
        설명
    </th>
    <tr>
        <td rowspan="3">1</td><td>description</td><td>N</td><td>권한에 대한 부가 설명</td>
    </tr>
    <tr>
        <td>name</td><td>Y</td><td>권한명(ex: ROLE_{name})</td>
    </tr>
    <tr>
        <td>parentId</td><td>N</td><td>부모 권한 식별자</td>
    </tr>
</table>
</details>

---
<br/><br/>


<details>
<summary>User API</summary>

<br/>

 - 기본 정보


id  | 메서드  | 요청 URL                                      | 응답                                | 설명
--- | ---     | ---                                           | ---                                 | ---
1   | POST     | /api/users/{userId}/roles/{roleId}           | 204                                 |  사용자 권한 등록
2   | DELETE     | /api/users/{userId}/roles/{roleId}           | 204                                 | 사용자 권한 삭제
3   | GET     | /api/users                | 200                                 | 사용자 리스트 조회 (uri variable(optional) : offset=value, size=value)
4   | GET  | /api/users/{userId}           | 200                                 | 사용자 한 건 조회

<br/><br/>

</details>

---
<br/><br/>


 - PermitAllFilter  
접근이 제한된 리소스 외에는 인증 및 인가처리가 필요하지 않습니다. 그리고 접근이 제한된 리소스인지 아닌지를 위에서 설명했던 UrlFilterInvocationSecurityMetadataSource에서 판별하는 것은 논리적 측면에서도, 복잡도 측면에서도 적절하지 않다고 생각했습니다.  
따라서 보호되지 않는 리소스에 대해서는 인증, 인가 프로세스를 수행하지 않도록 PermitAllFilter를 구현하였습니다.
<details>
<summary>일부 소스</summary>

```java
public class PermitAllFilter extends FilterSecurityInterceptor {

    private static final String FILTER_APPLIED = "__spring_security_filterSecurityInterceptor_filterApplied";

    private List<RequestMatcher> permitAllRequestMatcher = new ArrayList<>();

    private final AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;

    public PermitAllFilter(AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver, String... permitAllPattern) {
        createPermitAllPattern(permitAllPattern);
        this.authenticationManagerResolver = authenticationManagerResolver;
    }

    private void createPermitAllPattern(String[] permitAllPattern) {
        for(String pattern : permitAllPattern){
            //넘겨받은 pattern을 이용하여 RequestMatcher List 생성
            permitAllRequestMatcher.add(new AntPathRequestMatcher(pattern, HttpMethod.GET.name()));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        invoke(new FilterInvocation(request, response, chain));
    }

    public void invoke(FilterInvocation filterInvocation) throws IOException, ServletException {
        if (isApplied(filterInvocation) && super.isObserveOncePerRequest()) {
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
            return;
        } else {
            filterInvocation.getRequest().setAttribute(FILTER_APPLIED, Boolean.TRUE);
        }
        InterceptorStatusToken token = beforeInvocation(filterInvocation);
        try {
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
        }
        finally {
            super.finallyInvocation(token);
        }
        super.afterInvocation(token, null);
    }

    @Override
    protected InterceptorStatusToken beforeInvocation(Object object) {

        boolean isPermitAll = false;

        HttpServletRequest request = ((FilterInvocation) object).getRequest();
        for(RequestMatcher requestMatcher : permitAllRequestMatcher){
            if(requestMatcher.matches(request)){
                isPermitAll = true;
                break;
            }
        }
        if (isPermitAll){
            return null;
        }
        //FilterSecurityInterceptor에 등록된 AuthenticationManager와는 다른 객체를 사용하므로
        //AuthenticationManager 세팅
        //CustomJwtIssuerAuthenticationManagerResolver -> resolve AuthenticationManager -> JwtAuthenticationProvider
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication!=null && !authentication.isAuthenticated()) {
            super.setAuthenticationManager(authenticationManagerResolver.resolve(request));
        }

        return super.beforeInvocation(object);
    }
}
```
</details>

FilterSecurityInterceptor는 Spring Security에서 최종적으로 인증, 인가를 처리하는 핵심 객체입니다.  
이 객체를 확장하여 사전에 등록한 PermitAll Pattern에 해당된다면 더 이상 프로세스를 수행하지 않고 다음 필터로 넘어가게 됩니다. 

<br/><br/>


### 4. 개시판

각 게시판에는 해당 게시판을 관리하는 매니저가 있으며, 매니저 권한이 부여됩니다.(매니저의 게시판 관리 부분은 현재 미구현입니다. )  

다음은 게시판에 대한 API 명세입니다.

<details>
<summary>Board API</summary>

<br/>

 - 기본 정보

id  | 메서드  | 요청 URL                      | 응답                                | 설명
--- | ---     | ---                           | ---                                 | ---
1   | POST    | /api/boards                   | 201(Location : /api/boards/{id})    |  게시판 등록
2   | PUT     | /api/boards/{id}              | 204                                 |  게시판 수정(id : board 식별자)
3   | GET     | /api/boards/{id}              | 200                                 | 게시판 한 건 조회
4   | GET     | /api/boards                   | 200                                 | 게시판 리스트 조회 (uri variable(optional) : offset=value, size=value)
5   | DELETE  | /api/boards/{id}              | 204                                 | 게시판 삭제

<br/><br/>


 - 파라미터



<table>
    <th>
        id
    </th>
    <th>
        요청 변수명
    </th>
    <th>
        필수 여부
    </th>
    <th>
        설명
    </th>
    <tr>
        <td rowspan="10">1</td><td>category</td><td>Y</td><td>게시판 카테고리</td>
    </tr>
    <tr>
        <td>subject</td><td>Y</td><td>게시판 주제. 카테고리에 속하는 주제여야 합니다.</td>
    </tr>
    <tr>
        <td>user_id</td><td>Y</td><td>해당 게시판 관리자 권한을 부여할 사용자 id</td>
    </tr>
    <tr>
        <td>type</td><td>Y</td><td>현재 url방식만 허용. must be 'url'</td>
    </tr>
    <tr>
        <td>level</td><td>Y</td><td>리소스의 구체적인 수준에 비례한 값. 일반적인 경로보다 높아야 합니다.</td>
    </tr>
    <tr>
        <td>http_method</td><td>N</td><td>HttpMethod(GET, POST, DELETE, PUT) : null일시 모든 요청에 대해 매핑됩니다.</td>
    </tr>
    <tr>
        <td>id</td><td>Y</td><td>권한 식별자</td>
    </tr>
    <tr>
        <td>parent_id</td><td>N</td><td>부모 권한 식별자</td>
    </tr>
    <tr>
        <td>description</td><td>N</td><td>권한 부가 설명</td>
    </tr>
    <tr>
        <td>name</td><td>Y</td><td>권한명(ex: ROLE_{name})</td>
    </tr>
    <tr>
        <td rowspan="3">2</td><td>category</td><td>Y</td><td>게시판 카테고리</td>
    </tr>
    <tr>
        <td>subject</td><td>Y</td><td>게시판 주제. 카테고리에 속하는 주제여야 합니다.</td>
    </tr>
    <tr>
        <td>resource_id</td><td>Y</td><td>리소스 식별자</td>
    </tr>
</table>

</details>

<br/><br/>

> 게시판 등록의 경우 한 번의 요청으로 게시판, 리소스, 권한 세 자원에 대한 insert 처리가 이루어집니다.  
> 게시판을 등록한다면 해당 리소스들에 대해 추가하는 작업이 필수적이기 때문에 한 요청으로 여러 리소스를 조작하도록 하였습니다.   
> 
> 그런데 이렇게 구현을 해보았더니 리소스, 권한 리소스에 대한 표현이 누락되는 문제가 있음을 깨달았습니다. 그리고 요청이 실패했을 경우 어떤 부분에서 문제가 있었는지의 문제, 리소스와 권한의 Location을 반환하지 못하는 문제도 있었습니다.
>
> 여러 자원에 대한 조작이 한 프로세스로 묶여있다면 어떻게 설계하는 것이 좋을지 고민이 필요한 것 같습니다.


<br/><br/>

 - 게시판 등록과 트랜잭션

앞서 설명드린 것처럼 하나의 게시판 생성 요청은 여러 리소스 생성을 수반합니다.   
[BoardService.java](https://github.com/JaeHyeon-Kim94/board/blob/master/client/src/main/java/io/oauth2/client/board/BoardService.java)
```java
// BoardService
    @Transactional
    public Long addBoard(BoardCreateDto dto){

        Role role = BoardCreateDto.BoardRole.toRole(dto.getRole());
        roleRepository.addRole(role, dto.getRole().getParentId());

        userRepository.addUserRole(dto.getUserId(), role.getId());

        Resource resource = BoardCreateDto.BoardResource.toResource(dto.getResource());
        resource.setValue("/api/boards/"+dto.getCategory()+"/"+dto.getSubject()+"/management");
        resourceRepository.addResource(resource, role.getId());

        Board board = BoardCreateDto.toBoard(dto);
        boardRepository.addBoard(board, resource.getId());

        return board.getId();
    }
```

<br/><br/>

여러 리소스에 대한 등록 작업을 수행해야 하다 보니, 또 문제가 생겼습니다.
보통 Controller는 요청과 웹 관련 책임을 지고, 주요 비즈니스 로직은 서비스 레이어에서 구현하도록 되어있는데, BoardService 클래스에서 다른 서비스 의존성을 가지는 것이 괜찮은 것인지에 대한 고민이 생겼습니다.  

우선 Controller에서 여러 서비스를 호출하게 되면, 하나의 트랜잭션으로 묶을 수 없다는 단점이 있었습니다. 

그럼 한 서비스에서 여러 서비스를 의존한다면? 다른 서비스의 비즈니스적 구현사항을 그대로 사용할 수 있다는 장점은 있지만 상당히 큰 단점도 있다고 생각했습니다.

이러한 구조가 허용이 되어 기능 추가가 계속 된다면 순환참조문제를 항상 의식해야 한다는 문제가 있습니다. 물론 순환참조 문제가 발생한다는 것은 설계에 문제가 있다는 것으로 볼 수 있지만 어쨌든 항상 의식해야 한다는 페널티가 협업 측면에서도 그렇고 좋지 않게 다가왔습니다.

따라서 여러 서비스를 아우르는 서비스 객체를 새로 생성하여 각각의 서비스 로직을 호출하도록 구성하였습니다. 

[BoardResourceRoleService.java](https://github.com/JaeHyeon-Kim94/board/blob/master/client/src/main/java/io/oauth2/client/board/BoardResourceRoleService.java)

```java
@RequiredArgsConstructor
@Service
public class BoardResourceRoleService {

    private final UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;
    private final RoleService roleService;
    private final BoardService boardService;


    @Transactional
    public Long reloadRoleAndResourcesAfterAddBoard(BoardCreateDto dto){
        Long boardId = boardService.addBoard(dto);
        roleService.setRoleHierarchy();
        urlFilterInvocationSecurityMetadataSource.reload();
        return boardId;
    }

    @Transactional
    public void reloadRoleAndResourcesAfterUpdateBoard(BoardUpdateDto dto, Long boardId){
        boardService.updateBoard(dto, boardId);
        roleService.setRoleHierarchy();
        urlFilterInvocationSecurityMetadataSource.reload();
    }

    @Transactional
    public int reloadRoleAndResourcesAfterDeleteBoard(Long boardId){
        int result = boardService.deleteBoard(boardId);
        roleService.setRoleHierarchy();
        urlFilterInvocationSecurityMetadataSource.reload();
        return result;
    }
}
```

다만, 현재 Resource, Role에 대한 비즈니스 로직은 단순 CUD에 불과하여, BoardService에서 여러 repository에 대한 의존성을 갖도록 하고 Resource, Role을 reload하는 부분만을 호출하도록 하였습니다. 

그리고 해당 서비스 객체에서 시작한 트랜잭션은 다른 서비스들에게로 전파되는데, 등록이 정상적으로 수행되었음에도 reload 실패로 모두 롤백되는 것은 적절하지 않다고 판단해 reload의 경우 전파 전략을 NESTED로 설정하였습니다.




## 테스트

다음은 게시판 등록시 권한이 동적으로 반영되는 로직에 대한 테스트입니다.

<details>

<summary>일부 소스</summary>

```java
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class BoardTest extends BaseTest {

    private static final String BASE_URL = "/api/boards";

    private BoardCreateDto boardCreateDto;
    private Long id;

    @DisplayName("@BeforeEach - Board 추가 테스트 - 성공")
    @BeforeEach
    void beforeEach() throws Exception {
        //given
        BoardCreateDto.BoardRole boardRole
                = BoardCreateDto.BoardRole.builder()
                .id("M_TEST_BOARD_CATEGORY_SUBJECT")
                .name("ROLE_TEST_BOARD_CATEGORY_SUBJECT")
                .description("테스트 게시판 (카테고리 - 주제)")
                .parentId("M_0000")
                .build();

        BoardCreateDto.BoardResource boardResource
                = BoardCreateDto.BoardResource.builder()
                .type("url")
                .level(4100L)
                .build();

        boardCreateDto
                = BoardCreateDto.builder()
                .userId("test_manager")
                .category("test-category")
                .subject("test-subject")
                .role(boardRole)
                .resource(boardResource)
                .build();

        JwtAuthenticationToken token = getAuthentication("test_manager", "ROLE_ADMIN");

        String dto = om.writeValueAsString(boardCreateDto);

        //when
        MvcResult mvcResult = mvc.perform(post(BASE_URL)
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(dto))
                .andExpect(status().isCreated())
                .andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        id = Long.valueOf(location.replace(BASE_URL + "/", ""));
    }

    //Board 추가와 함께 설정된 권한처리 테스트
    @DisplayName("Board 권한 테스트")
    @Order(2)
    @Test
    void checkAuthority() throws Exception {
        //given
        String content = "{\"manageContent\" : \"TODO MANAGE CONTENT...\"}";


        JwtAuthenticationToken token = getAuthentication("test_manager", "ROLE_TEST_BOARD_CATEGORY_SUBJECT");

        MvcResult mvcResult = mvc.perform(post(BASE_URL + "/test-category/test-subject/management")
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> parsedResult = JSONObjectUtils.parse(mvcResult.getResponse().getContentAsString());
        assertThat((String)parsedResult.get("manageContent")).isEqualTo("TODO MANAGE CONTENT...");
    }


    private JwtAuthenticationToken getAuthentication(String sub, String authority){
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", sub)
                .claim("authorities", authority)
                .build();

        return new JwtAuthenticationToken(jwt, Set.of(new SimpleGrantedAuthority(authority)));
    }
}
```

</details>


<br/><br/>


Board 권한 테스트(checkAuthority)에서는 게시판과 함께 등록된 권한, 리소스에 대해 테스트하고 있습니다. 

등록한 게시판 관리 리소스에 대한 접근을 하기 위해 함께 등록된 권한 정보를 모킹하여 JWT 인증 객체를 생성후 요청을 보내고 있습니다.
