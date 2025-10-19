
package dev.miguelehr.conversordedivisas.data.repo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dev.miguelehr.conversordedivisas.data.model.ConversionRecord

class ConversionsRepo(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val col = db.collection("conversions")

    fun save(
        record: ConversionRecord,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        col.add(record)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun getMine(
        uid: String,
        limit: Long = 100,
        onSuccess: (List<ConversionRecord>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        col.whereEqualTo("uid", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { it.toObject(ConversionRecord::class.java) }
                onSuccess(list)
            }
            .addOnFailureListener { e -> onError(e) }
    }
}