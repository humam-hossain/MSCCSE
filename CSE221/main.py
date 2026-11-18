def maxSumSubArray(arr, n):
    if n == 1:
        return arr[0]
    
    mid = n//2

    leftSum = maxSumSubArray(arr[:mid], mid)
    rightSum = maxSumSubArray(arr[mid:], n - mid)
    crossLeft, crossRight, sum = float('-inf'), float('-inf'), 0

    for i in range(mid-1, -1, -1):
        sum = sum + arr[i]
        crossLeft = max(crossLeft, sum)

    sum =0
    for j in range(mid, n):
        sum = sum + arr[j]
        crossRight = max(crossRight, sum)

    crossSum = crossLeft + crossRight 

    return max(leftSum, rightSum, crossSum)

def karatsuba(x, y):
    if len(str(x)) == 1 or len(str(y)) == 1:
        return x*y
    
    n = max(len(str(x)), len(str(y)))
    m = (n + 1) // 2
    p = 10**m
    xL = x // p
    xR = x % p
    yL = y // p
    yR = y % p

    a = karatsuba(xL, yL)
    b = karatsuba(xR, yR)
    c = karatsuba(xL + xR, yL + yR)

    return a*(10**(2*m)) + (c - a - b) * (10**m) + b

arr = [3, -2, 5, -1]
n = len(arr)
print(maxSumSubArray(arr, n))

print(karatsuba(1234, 5678))
