package id.xxx.example.room.data.source.local.db.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import id.xxx.example.room.data.source.local.db.address.AddressEntity

@Entity(
    tableName = UserEntity.TABLE_NAME,
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_PK)
    val pk: Long = 0L,

    @ColumnInfo(name = COLUMN_NAME)
    val name: String,
) {
    companion object {
        const val TABLE_NAME = "user"
        const val COLUMN_PK = "pk"
        const val COLUMN_NAME = "name"
    }
}