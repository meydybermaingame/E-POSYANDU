$files = @(
    'app/src/main/java/com/example/e_posyandu/ui/screen/HomeScreen.kt',
    'app/src/main/java/com/example/e_posyandu/ui/screen/DataBalitaScreen.kt',
    'app/src/main/java/com/example/e_posyandu/ui/screen/InputBalitaScreen.kt',
    'app/src/main/java/com/example/e_posyandu/ui/screen/EditBalitaScreen.kt',
    'app/src/main/java/com/example/e_posyandu/ui/screen/DetailBalitaScreen.kt',
    'app/src/main/java/com/example/e_posyandu/ui/screen/PertumbuhanScreen.kt',
    'app/src/main/java/com/example/e_posyandu/ui/screen/ExportCsvScreen.kt'
)

foreach ($file in $files) {
    $content = Get-Content $file -Raw
    
    # Pattern untuk title dengan Text inline
    $pattern1 = 'title = \{ Text\(([^,]+), fontWeight = FontWeight\.Bold, fontSize = (\d+)\.sp\) \},'
    $replacement1 = @'
title = {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            $1,
                            fontWeight = FontWeight.Bold,
                            fontSize = $2.sp
                        )
                    }
                },
'@
    
    $content = $content -replace $pattern1, $replacement1
    
    Set-Content $file $content -NoNewline
    Write-Host "Updated: $file"
}
