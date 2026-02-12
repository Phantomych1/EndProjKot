package com.example.myapplication.data.repo

import com.example.myapplication.domain.model.BoardItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BoardRepo {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    fun ensureAnon(onOk: (String) -> Unit, onErr: () -> Unit) {
        val cur = auth.currentUser
        if (cur != null) { onOk(cur.uid); return }
        auth.signInAnonymously()
            .addOnSuccessListener { onOk(it.user?.uid.orEmpty()) }
            .addOnFailureListener { onErr() }
    }

    private fun ref(uid: String): DatabaseReference =
        db.getReference("boards").child(uid)

    fun observe(uid: String): Flow<List<BoardItem>> = callbackFlow {
        val r = ref(uid)
        val l = object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                val xs = s.children.mapNotNull { it.getValue(BoardItem::class.java) }
                    .sortedByDescending { it.createdAt }
                trySend(xs)
            }
            override fun onCancelled(e: DatabaseError) {}
        }
        r.addValueEventListener(l)
        awaitClose { r.removeEventListener(l) }
    }

    fun add(uid: String, text: String) {
        val t = text.trim()
        if (t.isEmpty()) return
        val r = ref(uid).push()
        val id = r.key ?: return
        r.setValue(BoardItem(id, t, System.currentTimeMillis(), uid))
    }

    fun update(uid: String, id: String, text: String) {
        val t = text.trim()
        if (t.isEmpty()) return
        ref(uid).child(id).updateChildren(mapOf("text" to t))
    }

    fun delete(uid: String, id: String) {
        ref(uid).child(id).removeValue()
    }
}
