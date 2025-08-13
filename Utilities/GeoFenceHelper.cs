using NetTopologySuite.Geometries;
using NetTopologySuite;


namespace SmartAlerts.API.Utilities
{
    public class GeoFenceHelper
    {
        private static readonly GeometryFactory _geometryFactory = NtsGeometryServices.Instance.CreateGeometryFactory(srid: 4326);

        // centerLat/Lon = geofence center
        // pointLat/Lon = user/device location
        public static bool IsWithinGeofence(double centerLat, double centerLon, double pointLat, double pointLon, double radiusMeters)
        {
            // Create points
            var center = _geometryFactory.CreatePoint(new Coordinate(centerLon, centerLat));
            var point = _geometryFactory.CreatePoint(new Coordinate(pointLon, pointLat));

            // Calculate Haversine distance manually
            double distanceMeters = GetHaversineDistance(centerLat, centerLon, pointLat, pointLon);

            return distanceMeters <= radiusMeters;
        }

        private static double GetHaversineDistance(double lat1, double lon1, double lat2, double lon2)
        {
            const double R = 6371000; // Earth radius in meters
            var latRad1 = DegreesToRadians(lat1);
            var latRad2 = DegreesToRadians(lat2);
            var deltaLat = DegreesToRadians(lat2 - lat1);
            var deltaLon = DegreesToRadians(lon2 - lon1);

            var a = Math.Sin(deltaLat / 2) * Math.Sin(deltaLat / 2) +
                    Math.Cos(latRad1) * Math.Cos(latRad2) *
                    Math.Sin(deltaLon / 2) * Math.Sin(deltaLon / 2);

            var c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
            return R * c;
        }

        private static double DegreesToRadians(double degrees) => degrees * Math.PI / 180;
    }
}
