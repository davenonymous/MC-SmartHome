package com.davenonymous.smarthome.api.sensor;

import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;

import java.util.Collection;

public interface SensorRange {
	default double min(HomeSensor<?,?> sensor, Collection<ISensorData> data) {
		return 0.0;
	}

	default boolean hasMin() {
		return false;
	}

	default double max(HomeSensor<?,?> sensor, Collection<ISensorData> data) {
		return 0.0;
	}

	default boolean hasMax() {
		return false;
	}

	record None() implements SensorRange {
	}

	record StaticMin(double min) implements SensorRange {
		@Override
		public double min(HomeSensor<?,?> sensor, Collection<ISensorData> data) {
			return min;
		}

		@Override
		public boolean hasMin() {
			return true;
		}
	}

	record StaticMax(double max) implements SensorRange {
		@Override
		public double max(HomeSensor<?,?> sensor, Collection<ISensorData> data) {
			return max;
		}

		@Override
		public boolean hasMax() {
			return true;
		}
	}

	record StaticMinMax(double min, double max) implements SensorRange {
		@Override
		public double min(HomeSensor<?,?> sensor, Collection<ISensorData> data) {
			return min;
		}

		@Override
		public boolean hasMin() {
			return true;
		}

		@Override
		public double max(HomeSensor<?,?> sensor, Collection<ISensorData> data) {
			return max;
		}

		@Override
		public boolean hasMax() {
			return true;
		}
	}

	record DynamicMin(SensorColumn column) implements SensorRange {
		@Override
		public double min(HomeSensor<?,?> sensor, Collection<ISensorData> dataList) {
			double foundMin = Double.POSITIVE_INFINITY;
			for(var data : dataList) {
				foundMin = Math.min(foundMin, sensor.valueFromData(HomeSensor.cast(data), column));
			}
			return foundMin;
		}

		@Override
		public boolean hasMin() {
			return true;
		}
	}

	record DynamicMax(SensorColumn column) implements SensorRange {
		@Override
		public double max(HomeSensor<?,?> sensor, Collection<ISensorData> dataList) {
			double foundMax = Double.NEGATIVE_INFINITY;
			for(var data : dataList) {
				foundMax = Math.max(foundMax, sensor.valueFromData(HomeSensor.cast(data), column));
			}
			return foundMax;
		}

		@Override
		public boolean hasMax() {
			return true;
		}
	}

	record DynamicMinMax(SensorColumn minColumn, SensorColumn maxColumn) implements SensorRange {
		@Override
		public double min(HomeSensor<?,?> sensor, Collection<ISensorData> dataList) {
			double foundMin = Double.POSITIVE_INFINITY;
			for(var data : dataList) {
				foundMin = Math.min(foundMin, sensor.valueFromData(HomeSensor.cast(data), minColumn));
			}
			return foundMin;
		}

		@Override
		public boolean hasMin() {
			return true;
		}

		@Override
		public double max(HomeSensor<?,?> sensor, Collection<ISensorData> dataList) {
			double foundMax = Double.NEGATIVE_INFINITY;
			for(var data : dataList) {
				foundMax = Math.max(foundMax, sensor.valueFromData(HomeSensor.cast(data), maxColumn));
			}
			return foundMax;
		}

		@Override
		public boolean hasMax() {
			return true;
		}
	}

	record StaticMinDynamicMax(double min, SensorColumn maxColumn) implements SensorRange {
		@Override
		public double min(HomeSensor<?,?> sensor, Collection<ISensorData> data) {
			return min;
		}

		@Override
		public boolean hasMin() {
			return true;
		}

		@Override
		public double max(HomeSensor<?,?> sensor, Collection<ISensorData> dataList) {
			double foundMax = Double.NEGATIVE_INFINITY;
			for(var data : dataList) {
				foundMax = Math.max(foundMax, sensor.valueFromData(HomeSensor.cast(data), maxColumn));
			}
			return foundMax;
		}

		@Override
		public boolean hasMax() {
			return true;
		}
	}

	record DynamicMinStaticMax(SensorColumn minColumn, double max) implements SensorRange {
		@Override
		public double min(HomeSensor<?,?> sensor, Collection<ISensorData> dataList) {
			double foundMin = Double.POSITIVE_INFINITY;
			for(var data : dataList) {
				foundMin = Math.min(foundMin, sensor.valueFromData(HomeSensor.cast(data), minColumn));
			}
			return foundMin;
		}

		@Override
		public boolean hasMin() {
			return true;
		}

		@Override
		public double max(HomeSensor<?,?> sensor, Collection<ISensorData> data) {
			return max;
		}

		@Override
		public boolean hasMax() {
			return true;
		}
	}
}
