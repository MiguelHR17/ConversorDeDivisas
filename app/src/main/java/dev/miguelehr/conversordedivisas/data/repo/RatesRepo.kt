
package dev.miguelehr.conversordedivisas.data.repo

import com.google.firebase.firestore.FirebaseFirestore
import dev.miguelehr.conversordedivisas.data.model.RateConfig

class RatesRepo(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun fetchRates(
        onSuccess: (Map<String, Double>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("rates").document("default").get()
            .addOnSuccessListener { snap ->
                val cfg = snap.toObject(RateConfig::class.java)
                onSuccess(cfg?.rates ?: emptyMap())
            }
            .addOnFailureListener { e -> onError(e) }
    }
}