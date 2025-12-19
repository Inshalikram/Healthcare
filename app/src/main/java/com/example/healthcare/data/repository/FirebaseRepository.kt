package com.example.healthcare.data.repository

import com.example.healthcare.data.model.Appointment
import com.example.healthcare.data.model.CartItem
import com.example.healthcare.data.model.Doctor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // Get current user
    fun getCurrentUser() = auth.currentUser

    // Get current user ID
    fun currentUserId(): String? = auth.currentUser?.uid

    // Register new user
    fun register(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    val exception = task.exception
                    val message = if (exception is FirebaseAuthException) {
                        "Auth Error (${exception.errorCode}): ${exception.message}"
                    } else {
                        exception?.localizedMessage ?: "Unknown registration error"
                    }
                    callback(false, message)
                }
            }
    }

    // Login existing user
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    val exception = task.exception
                    val message = if (exception is FirebaseAuthException) {
                        "Auth Error (${exception.errorCode}): ${exception.message}"
                    } else {
                        exception?.localizedMessage ?: "Unknown login error"
                    }
                    callback(false, message)
                }
            }
    }

    // Save user info to Firestore safely (Updated to handle doctor details)
    fun saveUserToFirestore(uid: String, name: String, email: String, role: String = "patient",
                            doctorDetails: Map<String, String> = emptyMap(),
                            callback: (Boolean, String?) -> Unit) {
        val userMap = mutableMapOf(
            "userId" to uid,
            "name" to name,
            "email" to email,
            "role" to role
        )
        
        if (doctorDetails.isNotEmpty()) {
            userMap.putAll(doctorDetails)
        }

        db.collection("users").document(uid)
            .set(userMap)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.localizedMessage) }
    }

    suspend fun getUserRole(uid: String): Result<String> {
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            val role = snapshot.getString("role") ?: "patient"
            Result.success(role)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    suspend fun getDoctors(): Result<List<Doctor>> {
        return try {
            // 1. Fetch real users who are doctors
            val userSnapshot = db.collection("users")
                .whereEqualTo("role", "doctor")
                .get()
                .await()

            val realDoctors = userSnapshot.documents.map { doc ->
                Doctor(
                    id = doc.getString("userId") ?: doc.id,
                    name = doc.getString("name") ?: "Unknown Doctor",
                    specialty = doc.getString("specialty") ?: "Family Physician", 
                    hospital = doc.getString("hospital") ?: "HealthCare Hospital",
                    experience = doc.getString("experience") ?: "5 Years",
                    mobile = doc.getString("mobile") ?: "1234567890",
                    fees = doc.getString("fees") ?: "500"
                )
            }

            // 2. Fetch static doctors (if any in 'doctors' collection)
            val docSnapshot = db.collection("doctors").get().await()
            val staticDoctors = docSnapshot.documents.map { doc ->
                Doctor(
                    id = doc.id,
                    name = doc.getString("name") ?: "Unknown",
                    specialty = doc.getString("specialty") ?: "General",
                    hospital = doc.getString("hospital") ?: "HealthCare Hospital",
                    experience = doc.getString("experience") ?: "5 Years",
                    mobile = doc.getString("mobile") ?: "N/A",
                    fees = doc.getString("fees") ?: "500"
                )
            }

            val allDoctors = ArrayList<Doctor>()
            allDoctors.addAll(realDoctors)
            allDoctors.addAll(staticDoctors)

            if (allDoctors.isEmpty()) {
                 val demoDoctors = listOf(
                    Doctor("1", "Dr. A. K. Gupta", "Family Physician", "City Hospital", "15 Years", "9876543210", "600"),
                    Doctor("2", "Dr. Sarah Jones", "Dentist", "Smile Clinic", "8 Years", "9876500000", "400"),
                    Doctor("3", "Dr. P. Sharma", "Cardiologists", "Heart Care", "20 Years", "9998887776", "1000"),
                    Doctor("4", "Dr. M. Ali", "Surgeon", "City Hospital", "12 Years", "8887776665", "800"),
                    Doctor("5", "Dr. Lisa Ray", "Dietician", "Wellness Center", "6 Years", "7776665554", "500"),
                    Doctor("6", "Dr. Steve Rogers", "Family Physician", "Community Health", "10 Years", "5554443322", "550")
                )
                Result.success(demoDoctors)
            } else {
                Result.success(allDoctors)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    suspend fun createAppointment(appointment: Appointment): Result<Unit> {
        return try {
            val docRef = db.collection("appointments").document()
            val appointmentMap = mapOf(
                "appointmentId" to docRef.id,
                "patientId" to appointment.patientId,
                "userName" to appointment.userName,
                "doctorId" to appointment.doctorId,
                "doctorName" to appointment.doctorName,
                "date" to appointment.date,
                "time" to appointment.time,
                "status" to appointment.status
            )
            
            docRef.set(appointmentMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    suspend fun getAppointments(userId: String): Result<List<Appointment>> {
        return try {
            val snapshot = db.collection("appointments")
                .whereEqualTo("patientId", userId)
                .get()
                .await()
                
            val list = snapshot.documents.map { doc ->
                Appointment(
                    appointmentId = doc.id,
                    patientId = doc.getString("patientId") ?: "",
                    userName = doc.getString("userName") ?: "",
                    doctorId = doc.getString("doctorId") ?: "",
                    doctorName = doc.getString("doctorName") ?: "",
                    date = doc.getString("date") ?: "",
                    time = doc.getString("time") ?: "",
                    status = doc.getString("status") ?: "pending"
                )
            }
            Result.success(list)
        } catch(e: Exception) {
             if (e is CancellationException) throw e
             Result.failure(e)
        }
    }

    suspend fun getDoctorAppointments(doctorId: String): Result<List<Appointment>> {
        return try {
            val snapshot = db.collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .get()
                .await()

            val list = snapshot.documents.map { doc ->
                Appointment(
                    appointmentId = doc.id,
                    patientId = doc.getString("patientId") ?: "",
                    userName = doc.getString("userName") ?: "",
                    doctorId = doc.getString("doctorId") ?: "",
                    doctorName = doc.getString("doctorName") ?: "",
                    date = doc.getString("date") ?: "",
                    time = doc.getString("time") ?: "",
                    status = doc.getString("status") ?: "pending"
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    suspend fun updateAppointmentStatus(appointmentId: String, status: String): Result<Unit> {
        return try {
            db.collection("appointments").document(appointmentId)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    suspend fun addToCart(cartItem: CartItem): Result<Unit> {
        return try {
            val cartMap = mapOf(
                "userId" to cartItem.userId,
                "productName" to cartItem.productName,
                "productPrice" to cartItem.productPrice,
                "productType" to cartItem.productType
            )
            db.collection("cart").add(cartMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    suspend fun getCartItems(userId: String): Result<List<CartItem>> {
        return try {
            val snapshot = db.collection("cart")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val items = snapshot.documents.map { doc ->
                CartItem(
                    id = doc.id,
                    userId = doc.getString("userId") ?: "",
                    productName = doc.getString("productName") ?: "",
                    productPrice = doc.getDouble("productPrice")?.toFloat() ?: 0f,
                    productType = doc.getString("productType") ?: ""
                )
            }
            Result.success(items)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    suspend fun deleteCartItem(itemId: String): Result<Unit> {
        return try {
            db.collection("cart").document(itemId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }
    
    suspend fun placeOrder(userId: String, items: List<CartItem>, total: Float): Result<Unit> {
        return try {
            val orderDetails = items.map { "${it.productName} (Price: ${it.productPrice})" }
            
            // Create a new document in 'orders' collection
            val orderMap = mapOf(
                "userId" to userId,
                "orderDate" to com.google.firebase.Timestamp.now(),
                "totalAmount" to total,
                "items" to orderDetails
            )
            db.collection("orders").add(orderMap).await()
            
            // Clear the user's cart
            val batch = db.batch()
            for (item in items) {
                val docRef = db.collection("cart").document(item.id)
                batch.delete(docRef)
            }
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    // For Notifications: Listen to appointment changes for a specific patient
    fun listenToAppointments(userId: String, onUpdate: (List<Appointment>) -> Unit): ListenerRegistration {
        return db.collection("appointments")
            .whereEqualTo("patientId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                
                val list = snapshot.documents.map { doc ->
                    Appointment(
                        appointmentId = doc.id,
                        patientId = doc.getString("patientId") ?: "",
                        userName = doc.getString("userName") ?: "",
                        doctorId = doc.getString("doctorId") ?: "",
                        doctorName = doc.getString("doctorName") ?: "",
                        date = doc.getString("date") ?: "",
                        time = doc.getString("time") ?: "",
                        status = doc.getString("status") ?: "pending"
                    )
                }
                onUpdate(list)
            }
    }
}
