import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.util.Log

// Test Firebase Connection
fun testFirebaseConnection() {
    val database = FirebaseDatabase.getInstance("https://e-posyandu-app-default-rtdb.asia-southeast1.firebasedatabase.app")
    val testRef = database.getReference("test")
    
    Log.d("FirebaseTest", "Testing Firebase connection...")
    
    // Test write
    testRef.setValue("Hello Firebase")
        .addOnSuccessListener {
            Log.d("FirebaseTest", "✅ Write successful")
            
            // Test read
            testRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(String::class.java)
                    Log.d("FirebaseTest", "✅ Read successful: $value")
                }
                
                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseTest", "❌ Read failed: ${error.message}")
                }
            })
        }
        .addOnFailureListener { exception ->
            Log.e("FirebaseTest", "❌ Write failed: ${exception.message}")
        }
}

// Test specific balita path
fun testBalitaPath() {
    val database = FirebaseDatabase.getInstance("https://e-posyandu-app-default-rtdb.asia-southeast1.firebasedatabase.app")
    val balitaRef = database.getReference("balita")
    
    Log.d("FirebaseTest", "Testing balita path...")
    
    balitaRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val count = snapshot.childrenCount
            Log.d("FirebaseTest", "✅ Balita count: $count")
            
            snapshot.children.forEach { child ->
                Log.d("FirebaseTest", "Balita ID: ${child.key}")
            }
        }
        
        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseTest", "❌ Balita read failed: ${error.message}")
        }
    })
}