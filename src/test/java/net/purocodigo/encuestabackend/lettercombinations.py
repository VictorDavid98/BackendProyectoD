def letterCombinations(digits: str) -> list[str]:
    digitsValues = [
        [], #1
        ["a","b","c"], #2
        ["d","e","f"], #3
        ["g","h","i"], #4
        ["j","k","l"], #5
        ["m","n","o"], #6
        ["p","q","r", "s"], #7
        ["t","u","v"], #8
        ["w","x","y", "z"] #9
    ]
    
    arr = list(digits)

    if len(arr) == 1:
        return digitsValues[int(arr[0]) - 1]

    val = []
    for i in range(0, len(arr)):
        val.append(digitsValues[int(arr[i]) - 1])

    finalList = []
    for i in range(0, len(val)):
        for k in range(0, len(val[i])):
            if i+1 < len(val):
                for j in range(0, len(val[i+1])):
                    finalList.append(val[i][k] + val[i+1][j])   

    return finalList

print(letterCombinations("234"))