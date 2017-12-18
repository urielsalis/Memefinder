package me.urielsalis.memefinder

import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.Serializer
import java.util.concurrent.ConcurrentMap

object DB {
    val db: DB = DBMaker.fileDB("memefinder.db").make()
    val map: ConcurrentMap<String, String> = db
            .hashMap("map", Serializer.STRING, Serializer.STRING)
            .createOrOpen()

    fun getDB(): ConcurrentMap<String, String> {
        return map
    }

    fun commit() {
        db.commit()
    }
}