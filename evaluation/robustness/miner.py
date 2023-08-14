import requests

# Method to compare two versions.
# Returns latest version
def compareVersions(v1, v2):
    # This will split both the versions by '.'
    arr1 = v1[1].split(".")
    arr2 = v2[1].split(".")
    n = len(arr1)
    m = len(arr2)

    # converts to integer from string
    arr1 = [int(i) for i in arr1]
    arr2 = [int(i) for i in arr2]

    # compares which list is bigger and fills
    # smaller list with zero (for unequal delimiters)
    if n > m:
        for i in range(m, n):
            arr2.append(0)
    elif m > n:
        for i in range(n, m):
            arr1.append(0)

    for i in range(len(arr1)):
        if arr1[i] > arr2[i]:
            return v1
        elif arr2[i] > arr1[i]:
            return v2
    return v1

# function that returns an object with the highest version in a list
def extractHighestVersion(versions):
    versionTuples = []

    for item in versions:

        versionTuples.append((item, versions[item]["openapiVer"]))
        #print(item)
        #versionContent = versions[item]
        #print(versionContent)
        #print(versionContent["openapiVer"])
        #print(versionContent["info"])

        #for versionItem in versionContent["info"]:
            #print(versionItem)
        #print("\n")
    mostRecent = versionTuples[0]

    for i in range(len(versionTuples) - 1):
        compareVersions(mostRecent ,versionTuples[i + 1])

    return versions[mostRecent[0]]


response = requests.request("GET", "https://api.apis.guru/v2/list.json")
response.encoding = "utf-8"

with open("apis.csv", "w+", encoding="utf8") as f:
    f.write("title;x-apisguru-categories;openapiVer;swaggerUrl\n")

    for spec in response.json():
        content = response.json()[spec]
        print(spec)
        if len(content["versions"]) > 1:
            version = extractHighestVersion(content["versions"])
        else:
            version = list(content["versions"].items())[0][1]
        info = version.get("info", None)
        openApiVersion = version.get("openapiVer", 0)
        swaggerURL = version.get("swaggerUrl", "")
        title = info.get("title", "")
        categories = info.get("x-apisguru-categories", "[]")
        line = title + ";" + str(categories) + ";" + openApiVersion + ";" + swaggerURL + "\n"
        f.write(line)

