// Debug script untuk memeriksa koneksi Firebase
// Tambahkan ke MainActivity atau HomeScreen untuk debugging

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

fun debugFirebaseConnection() {
    val database = FirebaseDatabase.getInstance("https://e-posyandu-app-default-rtdb.asia-southeast1.firebasedatabase.app")
    val testRef = database.getReference("test")
    
    Log.d("FirebaseDebug", "Mencoba koneksi ke Firebase...")
    
    // Test write
    testRef.setValue("Hello Firebase!").addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d("FirebaseDebug", "✅ Write berhasil!")
        } else {
            Log.e("FirebaseDebug", "❌ Write gagal: ${task.exception?.message}")
        }
    }
    
    // Test read
    testRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val value = snapshot.getValue(String::class.java)
            Log.d("FirebaseDebug", "✅ Read berhasil: $value")
        }
        
        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseDebug", "❌ Read gagal: ${error.message}")
        }
    })
    
    // Check balita data
    val balitaRef = database.getReference("balita")
    balitaRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.d("FirebaseDebug", "📊 Jumlah data balita: ${snapshot.childrenCount}")
            for (child in snapshot.children) {
                Log.d("FirebaseDebug", "   - ${child.key}: ${child.child("nama").getValue(String::class.java)}")
            }
        }
        
        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseDebug", "❌ Gagal membaca data balita: ${error.message}")
        }
    })
}

// Tambahkan ini ke onCreate MainActivity atau HomeScreen:
// debugFirebaseConnection()