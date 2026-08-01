package com.sana.app.core.database.dao

import androidx.room.*
import com.sana.app.core.database.entities.StudyPlanEntity
import kotlinx.coroutines.flow.Flow

/**
 * 🌿 SANA - DAO de Planes de Estudio
 */
@Dao
interface StudyPlanDao {

    @Query("SELECT * FROM study_plans WHERE creator_id = :userId ORDER BY timestamp DESC")
    fun getPlansByCreator(userId: Long): Flow<List<StudyPlanEntity>>

    @Query("SELECT * FROM study_plans WHERE visibility = 'PUBLIC' AND is_active = 1 ORDER BY timestamp DESC")
    fun getPublicPlans(): Flow<List<StudyPlanEntity>>

    @Query("SELECT * FROM study_plans WHERE subject = :subject AND is_active = 1 ORDER BY timestamp DESC")
    fun getPlansBySubject(subject: String): Flow<List<StudyPlanEntity>>

    @Query("SELECT * FROM study_plans WHERE share_code = :code LIMIT 1")
    suspend fun getPlanByShareCode(code: String): StudyPlanEntity?

    @Query("SELECT * FROM study_plans WHERE id = :id LIMIT 1")
    suspend fun getPlanById(id: Long): StudyPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: StudyPlanEntity): Long

    @Update
    suspend fun updatePlan(plan: StudyPlanEntity)

    @Query("DELETE FROM study_plans WHERE id = :planId")
    suspend fun deletePlan(planId: Long)
}