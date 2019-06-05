package pl.edu.agh.financeservice.repository;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.agh.financeservice.model.Activity;

import java.util.Date;
import java.util.Set;

@Repository
public interface ActivityRepository extends CrudRepository<Activity, String> {

    @Query("SELECT * FROM activity WHERE trainingId = :trainingId ALLOW FILTERING")
    Set<Activity> findAllTrainingActivity(@Param("trainingId") String trainingId);

    @Query("SELECT MIN(startdate) FROM activity WHERE trainingId = :trainingId ALLOW FILTERING")
    Date findTrainingStartDate(@Param("trainingId") String trainingId);

    @Query("SELECT MAX(enddate) FROM activity WHERE trainingId = :trainingId ALLOW FILTERING")
    Date findTrainingEndDate(@Param("trainingId") String trainingId);

    @Query("SELECT SUM(calories) FROM activity WHERE trainingId = :trainingId ALLOW FILTERING")
    Double calculateCaloriesPerTraining(@Param("trainingId") String trainingId);

    @Query("SELECT SUM(distance) FROM activity WHERE trainingId = :trainingId ALLOW FILTERING")
    Double calculateDistancePerTraining(@Param("trainingId") String trainingId);

    @Query("SELECT SUM(steps) FROM activity WHERE trainingId = :trainingId ALLOW FILTERING")
    Integer calculateStepsPerTraining(@Param("trainingId") String trainingId);

    @Query("SELECT AVG(speed) FROM activity WHERE trainingId = :trainingId ALLOW FILTERING")
    Double calculateAverageSpeedPerTraining(@Param("trainingId") String trainingId);

    @Query("SELECT MIN(speed) FROM activity WHERE trainingId = :trainingId ALLOW FILTERING")
    Double findMinSpeedRatePerTraining(@Param("trainingId") String trainingId);

    @Query("SELECT MAX(speed) FROM activity WHERE trainingId = :trainingId ALLOW FILTERING")
    Double findMaxSpeedPerTraining(@Param("trainingId") String trainingId);

    @Query("SELECT MIN(heartrate) FROM activity WHERE trainingId = :trainingId ALLOW FILTERING")
    Integer findMinHeartRatePerTraining(@Param("trainingId") String trainingId);

    @Query("SELECT MAX(heartrate) FROM activity WHERE trainingId = :trainingId ALLOW FILTERING")
    Integer findMaxHeartRatePerTraining(@Param("trainingId") String trainingId);

    @Query("SELECT AVG(heartrate) FROM activity WHERE trainingId = :trainingId ALLOW FILTERING")
    Double findMeanHeartRatePerTraining(@Param("trainingId") String trainingId);

    @Query("SELECT AVG(spo2) FROM activity WHERE trainingId = :trainingId ALLOW FILTERING")
    Double calculateMeanOfSpo2(@Param("trainingId") String trainingId);
}
