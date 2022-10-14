# Implemented Rules
All rule descriptions and examples are taken from the book of Massé [1]. Link to the book as pdf can be found in the repo: [docs/Paper/REST API design rulebook](https://github.com/manuelmerkel/Projektarbeit-Master/blob/ce74f66fc6e0059bcc3ecb406d0da97456df3f2b/docs/Paper/REST%20API%20design%20rulebook%20%5Bdesigning%20consistent%20RESTful%20web%20service%20interfaces%5D%20(Mass%C3%A9,%20Mark)%20(z-lib.org).pdf).
### 1. Content-Type must be used
#### Description
The Content-Type header names the type of data found within a request or response message’s body. The value of this header is a specially formatted text string known as a media type [...]. Clients and servers rely on this header’s value to tell them how to process the sequence of bytes in a message’s body.
### 2. CRUD function names should not be used in URIs
#### Description
URIs should not be used to indicate that a CRUD§ function is performed. URIs should be used to uniquely identify resources [...]. [...] HTTP request methods should be used to indicate which CRUD function is performed.
#### Examples
For example, this API interaction design is preferred:
* DELETE /users/1234

The following anti-patterns exemplify what not to do:
* GET /deleteUser?id=1234
* GET /deleteUser/1234
* DELETE /deleteUser/1234
* POST /users/1234/delete
### 3. File extensions should not be included in URIs
#### Description
On the Web, the period (.) character is commonly used to separate the file name and extension portions of a URI. A REST API should not include artificial file extensions in URIs to indicate the format of a message’s entity body. Instead, they should rely on the media type, as communicated through the Content-Type header, to determine how to process the body’s content.
#### Examples
* http://api.college.restapi.org/students/3248234/transcripts/2005/fall.json <sup>1</sup>
* http://api.college.restapi.org/students/3248234/transcripts/2005/fall <sup>2</sup>

<sup>1</sup> File extensions should not be used to indicate format preference.

<sup>2</sup> REST API clients should be encouraged to utilize HTTP’s provided format selection mechanism, the Accept request header.
### 4. GET must be used to retrieve a representation of a resource
#### Description
A REST API client uses the GET method in a request message to retrieve the state of a resource, in some representational form. A client’s GET request message may contain headers but no body. The architecture of the Web relies heavily on the nature of the GET method. Clients count on being able to repeat GET requests without causing side effects. Caches depend on the ability to serve cached representations without contacting the origin server.
### 5. Hyphens (-) should be used to improve the readability of URIs
#### Description
To make your URIs easy for people to scan and interpret, use the hyphen (-) character to improve the readability of names in long path segments. Anywhere you would use a space or hyphen in English, you should use a hyphen in a URI.
#### Example
* http://api.example.restapi.org/blogs/mark-masse/entries/this-is-my-first-post
### 6. Lowercase letters should be preferred in URI paths
#### Description
When convenient, lowercase letters are preferred in URI paths since capital letters can sometimes cause problems. RFC 3986 defines URIs as case-sensitive except for the scheme and host components.
#### Examples
* http://api.example.restapi.org/my-folder/my-doc <sup>1</sup>
* HTTP://API.EXAMPLE.RESTAPI.ORG/my-folder/my-doc <sup>2</sup>
* http://api.example.restapi.org/My-Folder/my-doc <sup>3</sup>

<sup>1</sup> This URI is fine.

<sup>2</sup> The URI format specification (RFC 3986) considers this URI to be identical to URI #1.

<sup>3</sup> This URI is not the same as URIs #1 and #2, which may cause unnecessary confusion.
### 7. A plural noun should be used for collection/store names
#### Description
A URI identifying a collection/store of resources should be named with a plural noun, or noun phrase, path segment. A collection’s name should be chosen to reflect what it uniformly contains.
#### Example collection names
A collection’s name should be chosen to reflect what it uniformly contains. For example, the URI for a collection of player documents uses the plural noun form of its contained resources:
* http://api.soccer.restapi.org/leagues/seattle/teams/trebuchet/players

#### Example store names
The URI for a store of music playlists may use the plural noun form as follows:
* http://api.music.restapi.org/artists/mikemassedotcom/playlists
### 8. Forward slash separator (/) must be used to indicate a hierarchical relationship
#### Description
The forward slash (/) character is used in the path portion of the URI to indicate a hierarchical relationship between resources.
#### Example
* http://api.canvas.restapi.org/shapes/polygons/quadrilaterals/squares
### 9. A singular noun should be used for document names
#### Description
A URI representing a document resource should be named with a singular noun or noun phrase path segment.
#### Example
For example, the URI for a single player document would have the singular form:
* http://api.soccer.restapi.org/leagues/seattle/teams/trebuchet/players/claudio
### 10. A trailing forward slash (/) should not be included in URIs
#### Description & Examples
As the last character within a URI’s path, a forward slash (/) adds no semantic value and may cause confusion. REST APIs should not expect a trailing slash and should not include them in the links that they provide to clients. 

Many web components and frameworks will treat the following two URIs equally:
* http://api.canvas.restapi.org/shapes/ 
* http://api.canvas.restapi.org/shapes  

However, every character within a URI counts toward a resource’s unique identity. Two different URIs map to two different resources. If the URIs differ, then so do the resources, and vice versa. Therefore, a REST API must generate and communicate clean URIs and should be intolerant of any client’s attempts to identify a resource imprecisely. More forgiving APIs may redirect clients to URIs without a trailing forward slash
### 11. GET and POST must not be used to tunnel other request methods
#### Description
Tunneling refers to any abuse of HTTP that masks or misrepresents a message’s intent and undermines the protocol’s transparency. A REST API must not compromise its design by misusing HTTP’s request methods in an effort to accommodate clients with limited HTTP vocabulary. Always make proper use of the HTTP methods as specified by the rules in this section.
### 12. 401 (\"Unauthorized\") must be used when there is a problem with the client's credentials
#### Description
A 401 error response indicates that the client tried to operate on a protected resource without providing the proper authorization. It may have provided the wrong credentials or none at all.
### 13. Underscores (\_) should not be used in URI
#### Description
Text viewer applications (browsers, editors, etc.) often underline URIs to provide a visual cue that they are clickable. Depending on the application’s font, the underscore (\_) character can either get partially obscured or completely hidden by this underlining. To avoid this confusion, use hyphens (-) instead of underscores
### 14. A verb or verb phrase should be used for controller names
#### Description
Like a computer program’s function, a URI identifying a controller resource should be named to indicate its action.
#### Examples
* http://api.college.restapi.org/students/morgan/register
* http://api.example.restapi.org/lists/4324/dedupe
* http://api.ognom.restapi.org/dbs/reindex
* http://api.build.restapi.org/qa/nightly/runTestSuite


## Source
[1] https://www.oreilly.com/library/view/rest-api-design/9781449317904/
