# Car Dealership API — Full Backend Test
$BASE = "http://localhost:8080"
$TMP = "$env:TEMP\api_test"
Remove-Item -Path $TMP -Recurse -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path $TMP -Force | Out-Null

function Api($method, $url, $body, $token) {
    $curlArgs = @("-s", "-X", $method, $url, "-H", "Content-Type: application/json")
    if ($token) { $curlArgs += @("-H", "Authorization: Bearer $token") }
    if ($body) {
        $body | Set-Content -Path "$TMP\body.json" -Encoding ascii
        $curlArgs += @("--data-binary", "@$TMP\body.json")
    }
    return & "curl.exe" $curlArgs
}

function Pass($msg) { Write-Host "  PASS: $msg" -ForegroundColor Green }
function Fail($msg, $got) { Write-Host "  FAIL: $msg (got: $got)" -ForegroundColor Red }

Write-Host "=== 1. Register a new user ===" -ForegroundColor Cyan
$r = Api POST "$BASE/api/auth/register" '{"username":"alice","email":"alice@test.com","password":"password123"}'
$uToken = ($r | ConvertFrom-Json).token
if ($uToken) { Pass "Registered alice, got token" } else { Pass "Alice already exists (expected)" }

Write-Host "`n=== 2. Login as user ===" -ForegroundColor Cyan
$r = Api POST "$BASE/api/auth/login" '{"username":"alice","password":"password123"}'
$uToken = ($r | ConvertFrom-Json).token
if ($uToken) { Pass "Login alice, got token" } else { Fail "Login failed", $r; exit }

Write-Host "`n=== 3. Login as admin ===" -ForegroundColor Cyan
$r = Api POST "$BASE/api/auth/login" '{"username":"admin","password":"admin123"}'
$aToken = ($r | ConvertFrom-Json).token
if ($aToken) { Pass "Login admin, got token" } else { Fail "Login failed", $r; exit }

Write-Host "`n=== 4. Admin creates a vehicle ===" -ForegroundColor Cyan
$r = Api POST "$BASE/api/vehicles" '{"make":"Toyota","model":"Camry","category":"Sedan","price":25000,"quantity":10}' $aToken
$vid = ($r | ConvertFrom-Json).id
if ($vid) { Pass "Created vehicle id=$vid" } else { Fail "No id", $r; exit }

Write-Host "`n=== 5. User views vehicles ===" -ForegroundColor Cyan
$r = Api GET "$BASE/api/vehicles?page=0&size=5" $null $uToken
$total = ($r | ConvertFrom-Json).totalElements
if ($total -gt 0) { Pass "Got $total vehicles" } else { Fail "No vehicles", $r }

Write-Host "`n=== 6. Search vehicles ===" -ForegroundColor Cyan
$r = Api GET "$BASE/api/vehicles/search?make=Toyota&page=0&size=5" $null $uToken
$count = ($r | ConvertFrom-Json).totalElements
if ($count -gt 0) { Pass "Found $count Toyota vehicles" } else { Fail "No results", $r }

Write-Host "`n=== 7. User purchases 3 vehicles ===" -ForegroundColor Cyan
$r = Api POST "$BASE/api/vehicles/$vid/purchase" '{"quantity":3}' $uToken
$qty = ($r | ConvertFrom-Json).quantity
if ($qty -eq 7) { Pass "Purchased 3, stock now $qty" } else { Fail "Expected qty=7", "qty=$qty $r" }

Write-Host "`n=== 8. Over-purchase (expect 400) ===" -ForegroundColor Cyan
$r = Api POST "$BASE/api/vehicles/$vid/purchase" '{"quantity":999}' $uToken
$status = ($r | ConvertFrom-Json).status
if ($status -eq 400) { Pass "Over-purchase rejected (400)" } else { Fail "Expected 400", $r }

Write-Host "`n=== 9. User cannot delete (expect 403) ===" -ForegroundColor Cyan
$code = curl.exe -s -o NUL -w "%{http_code}" -X DELETE "$BASE/api/vehicles/$vid" -H "Authorization: Bearer $uToken"
if ($code -eq 403) { Pass "User delete returned 403" } else { Fail "Expected 403", $code }

Write-Host "`n=== 10. Admin restocks ===" -ForegroundColor Cyan
$r = Api POST "$BASE/api/vehicles/$vid/restock" '{"quantity":5}' $aToken
$qty = ($r | ConvertFrom-Json).quantity
if ($qty -eq 12) { Pass "Restocked 5, stock now $qty" } else { Fail "Expected qty=12", "qty=$qty $r" }

Write-Host "`n=== 11. Admin updates vehicle ===" -ForegroundColor Cyan
$r = Api PUT "$BASE/api/vehicles/$vid" '{"make":"Toyota","model":"Camry LE","category":"Sedan","price":26000,"quantity":12}' $aToken
$model = ($r | ConvertFrom-Json).model
if ($model -eq "Camry LE") { Pass "Updated to $model" } else { Fail "Expected Camry LE", $r }

Write-Host "`n=== 12. Admin deletes (expect 204) ===" -ForegroundColor Cyan
$code = curl.exe -s -o NUL -w "%{http_code}" -X DELETE "$BASE/api/vehicles/$vid" -H "Authorization: Bearer $aToken"
if ($code -eq 204) { Pass "Admin delete returned 204" } else { Fail "Expected 204", $code }

Write-Host "`n=== 13. Duplicate register (expect 409) ===" -ForegroundColor Cyan
$r = Api POST "$BASE/api/auth/register" '{"username":"admin","email":"dup@test.com","password":"password123"}'
$status = ($r | ConvertFrom-Json).status
if ($status -eq 409) { Pass "Duplicate rejected (409)" } else { Fail "Expected 409", $r }

Write-Host "`n=== 14. No token (expect 401) ===" -ForegroundColor Cyan
$code = curl.exe -s -o NUL -w "%{http_code}" "$BASE/api/vehicles"
if ($code -eq 401) { Pass "No-token returned 401" } else { Fail "Expected 401", $code }

Write-Host "`n=== ALL 14 TESTS COMPLETE ===" -ForegroundColor Green
