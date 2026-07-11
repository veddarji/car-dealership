# Car Dealership API Manual Test Script (PowerShell)
# Run while app is running (mvn spring-boot:run)

$BASE = "http://localhost:8080"

# Login helper
function Get-Token($user, $pass) {
    $body = "{`"username`":`"$user`",`"password`":`"$pass`"}"
    $body | Set-Content -Path "$env:TEMP\login_req.json" -Encoding ascii
    $r = curl.exe -s -X POST "$BASE/api/auth/login" -H "Content-Type: application/json" --data-binary "@$env:TEMP\login_req.json"
    ($r | ConvertFrom-Json).token
}

Write-Host "=== Get tokens ===" -ForegroundColor Cyan
$USER_TOKEN = Get-Token "testuser" "password123"
$ADMIN_TOKEN = Get-Token "admin" "admin123"
Write-Host "User token: $($USER_TOKEN.Substring(0,20))..." -ForegroundColor Green
Write-Host "Admin token: $($ADMIN_TOKEN.Substring(0,20))..." -ForegroundColor Green

Write-Host "`n=== 1. Admin creates vehicle (expect 201) ===" -ForegroundColor Cyan
$body = '{"make":"Toyota","model":"Camry","category":"Sedan","price":25000,"quantity":10}'
$body | Set-Content -Path "$env:TEMP\create.json" -Encoding ascii
$r = curl.exe -s -X POST "$BASE/api/vehicles" -H "Authorization: Bearer $ADMIN_TOKEN" -H "Content-Type: application/json" --data-binary "@$env:TEMP\create.json"
Write-Host $r -ForegroundColor Green
$VID = ($r | ConvertFrom-Json).id

Write-Host "`n=== 2. User views vehicles ===" -ForegroundColor Cyan
$r = curl.exe -s -G "$BASE/api/vehicles" -H "Authorization: Bearer $USER_TOKEN" -d "page=0" -d "size=3"
Write-Host ($r | ConvertFrom-Json | ConvertTo-Json -Depth 10)

Write-Host "`n=== 3. User purchases vehicle (expect 200) ===" -ForegroundColor Cyan
$body = '{"quantity":1}'
$body | Set-Content -Path "$env:TEMP\purchase.json" -Encoding ascii
$r = curl.exe -s -X POST "$BASE/api/vehicles/$VID/purchase" -H "Authorization: Bearer $USER_TOKEN" -H "Content-Type: application/json" --data-binary "@$env:TEMP\purchase.json"
Write-Host $r -ForegroundColor Green

Write-Host "`n=== 4. User buys too many (expect 400) ===" -ForegroundColor Cyan
$body = '{"quantity":999}'
$body | Set-Content -Path "$env:TEMP\overbuy.json" -Encoding ascii
$r = curl.exe -s -X POST "$BASE/api/vehicles/$VID/purchase" -H "Authorization: Bearer $USER_TOKEN" -H "Content-Type: application/json" --data-binary "@$env:TEMP\overbuy.json"
Write-Host $r -ForegroundColor Yellow

Write-Host "`n=== 5. User deletes (expect 403) ===" -ForegroundColor Cyan
$r = curl.exe -s -o NUL -w "%{http_code}" -X DELETE "$BASE/api/vehicles/$VID" -H "Authorization: Bearer $USER_TOKEN"
Write-Host "HTTP $r" -ForegroundColor Yellow

Write-Host "`n=== 6. Admin deletes (expect 204) ===" -ForegroundColor Cyan
$r = curl.exe -s -o NUL -w "%{http_code}" -X DELETE "$BASE/api/vehicles/$VID" -H "Authorization: Bearer $ADMIN_TOKEN"
Write-Host "HTTP $r" -ForegroundColor Green

Write-Host "`n=== 7. Duplicate register (expect 409) ===" -ForegroundColor Cyan
$body = '{"username":"admin","email":"other@test.com","password":"password123"}'
$body | Set-Content -Path "$env:TEMP\dup.json" -Encoding ascii
$r = curl.exe -s -X POST "$BASE/api/auth/register" -H "Content-Type: application/json" --data-binary "@$env:TEMP\dup.json"
Write-Host $r -ForegroundColor Yellow

Write-Host "`n=== 8. Search vehicles ===" -ForegroundColor Cyan
$r = curl.exe -s -G "$BASE/api/vehicles/search" -H "Authorization: Bearer $USER_TOKEN" -d "make=Toyota" -d "page=0" -d "size=5"
Write-Host ($r | ConvertFrom-Json | ConvertTo-Json -Depth 10)

Write-Host "`n=== 9. Swagger UI ===" -ForegroundColor Cyan
$r = curl.exe -s -o NUL -w "%{http_code}" -L "http://localhost:8080/swagger-ui.html"
Write-Host "HTTP $r" -ForegroundColor Green

Write-Host "`n=== ALL DONE ===" -ForegroundColor Green
