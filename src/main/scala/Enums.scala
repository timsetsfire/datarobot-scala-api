package io.github.timsetsfire.datarobot.enums
import com.fasterxml.jackson.annotation.JsonProperty
import scala.reflect.ClassTag
import org.json4s._
import org.json4s.ext.EnumNameSerializer

object EnumFormats {
    val enumFormats = Seq(
        new EnumNameSerializer(AccuracyMetric),
        new EnumNameSerializer(BlenderMethod),
        new EnumNameSerializer(CVMethod),
        new EnumNameSerializer(DateExtractionUnits),
        new EnumNameSerializer(ModelingMode),
        new EnumNameSerializer(Source),
        new EnumNameSerializer(TargetType),
        new EnumNameSerializer(ValidationType),
        new EnumNameSerializer(VariableTypeTransform),
        new EnumNameSerializer(FeatureAssociationMetric),
        new EnumNameSerializer(FeatureAssociationType),
        new EnumNameSerializer(JobType),
        new EnumNameSerializer(ModelRecommendationType)
    )
}

object AccuracyMetric extends Enumeration { 
    type AccuracyMetric = Value
    val ACCURACY = Value("Accuracy")
    val AUC = Value("AUC")
    val BALANCED_ACCURACY = Value("Balanced Accuracy")
    val FVE_BINOMIAL = Value("FVE Binomial")
    val GINI_NORM = Value("Gini Norm")
    val KOLMOGOROV_SMIRNOV = Value("Kolmogorov-Smirnov")
    val LOGLOSS = Value("LogLoss")
    val RATE_TOP5 = Value("Rate@Top5%")
    val RATE_TOP10 = Value("Rate@Top10%")
    val GAMMA_DEVIANCE = Value("Gamma Deviance")
    val FVE_GAMMA = Value("FVE Gamma")
    val FVE_POISSON = Value("FVE Poisson")
    val FVE_TWEEDIE = Value("FVE Tweedie")
    val MAD = Value("MAD")
    val MAE = Value("MAE")
    val MAPE = Value("MAPE")
    val POISSON_DEVIANCE = Value("Poisson Deviance")
    val R_SQUARED = Value("R Squared")
    val RMSE = Value("RMSE")
    val RMSLE = Value("RMSLE")
    val TWEEDIE_DEVIANCE = Value("Tweedie Deviance")
    val ALL_CLASSIFICATION = Seq(
        ACCURACY, AUC, BALANCED_ACCURACY, FVE_BINOMIAL, GINI_NORM,
        KOLMOGOROV_SMIRNOV, LOGLOSS, RATE_TOP5, RATE_TOP10
    )
    val ALL_REGRESSION = Seq(
        GAMMA_DEVIANCE, FVE_GAMMA, FVE_POISSON, FVE_TWEEDIE, MAD, MAE, MAPE,
        POISSON_DEVIANCE, R_SQUARED, RMSE, RMSLE, TWEEDIE_DEVIANCE
    )
    val ALL = Seq(
        ACCURACY, AUC, BALANCED_ACCURACY, FVE_BINOMIAL, GINI_NORM,
        KOLMOGOROV_SMIRNOV, LOGLOSS, RATE_TOP5, RATE_TOP10,
        GAMMA_DEVIANCE, FVE_GAMMA, FVE_POISSON, FVE_TWEEDIE, MAD, MAE, MAPE,
        POISSON_DEVIANCE, R_SQUARED, RMSE, RMSLE, TWEEDIE_DEVIANCE
    )
}

object BlenderMethod extends Enumeration { 
    type BlenderMethod = Value 
    val AVERAGE=Value("AVG")
    val ENET=Value("ENET")
    val GLM=Value("GLM")
    val MAE=Value("MAE")
    val MAEL1=Value("MAEL1")
    val MEDIAN=Value("MED")
    val PLS=Value("PLS")
    val RANDOM_FOREST=Value("RF")
    val LIGHT_GBM=Value("LGBM")
    val TENSORFLOW=Value("TF")
    val FORECAST_DISTANCE_ENET=Value("FORECAST_DISTANCE_ENET")
    val FORECAST_DISTANCE_AVG=Value("FORECAST_DISTANCE_AVG")
}


object CVMethod extends Enumeration {
        type CVMethod = Value
        val DATETIME = Value("datetime")
        val RANDOM = Value("random")
        val STRATIFIED = Value("stratified")
        val USER = Value("user")
        val GROUP = Value("group")
    }



object DateExtractionUnits extends Enumeration { 
    type DateExtractionUnits = Value 
    val YEAR = Value("year")
    val YEARDAY =  Value("yearDay")
    val MONTH =  Value("month")
    val MONTHDAY =  Value("monthDay")
    val WEEK =  Value("week")
    val WEEKDAY =  Value("weekDay")
}

object FeatureAssociationMetric extends Enumeration {
        type FeatureAssociationMetric = Value
        val MUTUALINFO = Value("mutualInfo")
        val CRAMERSV = Value("cramersV")
        val PEARSON = Value("pearson")
        val SPEARMAN = Value("spearman")
        val TAU = Value("tau")
    }

object FeatureAssociationType extends Enumeration {
        type FeatureAssociationType = Value
        val ASSOCIATION = Value("association")
        val CORRELATION = Value("correlation")
    }

object JobType extends Enumeration { 
    type JobType = Value
    val FEATURE_IMPACT = Value("featureImpact")
    val FEATURE_EFFECTS = Value("featureEffects")
    val FEATURE_FIT = Value("featureFit")
    val MODEL = Value("model")
    val MODEL_EXPORT = Value("modelExport")
    val PREDICT = Value("predict")
    val TRAINING_PREDICTIONS = Value("trainingPredictions")
    val PRIME_MODEL = Value("primeModel")
    val PRIME_RULESETS = Value("primeRulesets")
    val PRIME_VALIDATION = Value("primeDownloadValidation")
    val REASON_CODES = Value("reasonCodes")
    val REASON_CODES_INITIALIZATION = Value("reasonCodesInitialization")
    val PREDICTION_EXPLANATIONS = Value("predictionExplanations")
    val PREDICTION_EXPLANATIONS_INITIALIZATION = Value("predictionExplanationsInitialization")
    val RATING_TABLE_VALIDATION = Value("validateRatingTable")
    val SERIES_ACCURACY = Value("series_accuracy")
}
    

object DefaultTimeout extends Enumeration { 
    type DefaultTimeout = Value
    val DEFAULT_MAX_WAIT = Value(600000)
    val CONNECT = Value(605) // # time in seconds for the connection to server to be established
    val READ = Value(60000)      // # time in seconds after which to conclude the server isn't responding anymore
}



object ModelingMode extends Enumeration { 
    type ModelingMode = Value 
    val AUTOPILOT = Value("auto")
    val QUICKRUN = Value("quick")
    val MANUAL = Value("manual")
    val COMPREHENSIVE = Value("comprehensive")
}

object ModelRecommendationType extends Enumeration { 
    type ModelRecommendationType = Value
    val MOST_ACCURATE = Value("Most Accurate")
    val FAST_AND_ACCURATE = Value("Fast & Accurate")
    val RECOMMENDED = Value("Recommended for Deployment")
}

object ProjectStage extends Enumeration {
    type ProjectStage = Value
    val AIM=Value("aim")
    val EDA=Value("eda")
    val EMPTY=Value("empty")
    val MODELING=Value("modeling")
}

object SharingRole extends Enumeration { 
    type SharingRole = Value 
    val OWNER= Value("OWNER")
    val READ_WRITE= Value("READ_WRITE")
    val USER= Value("USER")
    val EDITOR= Value("EDITOR")
    val READ_ONLY= Value("READ_ONLY")
    val CONSUMER= Value("CONSUMER")
}

object Source extends Enumeration { 
    type Source = Value 
    val HOLDOUT = Value("holdout")
    val CV = Value("crossValidation")
    val VALIDATION = Value("validation")
}
    

object TargetType extends Enumeration { 
    type TargetType = Value
    val BINARY = Value("binary")
    val MULTICLASS = Value("multiclass")
    val REGRESSION = Value("regression")
}

object ValidationType extends Enumeration { 
    type ValidationType = Value 
    val CV, TVH = Value
}

object VariableTypeTransform extends Enumeration { 
    type VariableTypeTransform = Value 
    val CATEGORICAL = Value("categorical")
    val CATEGORICAL_INT = Value("categoricalInt")
    val NUMERIC = Value("numeric")
    val TEXT = Value("text")
}
