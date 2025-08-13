using System;
using System.Net.Http;
using System.Text.Json;
using System.Threading.Tasks;

namespace SmartAlerts.API.Utilities
{
    public class LatitudeLongitudeGenerator
    {
        private const string UserAgent = "SmartAlertsApp/1.0 (ayirp0117@gmail.com)";
        private const string BaseUrl = "https://nominatim.openstreetmap.org/search";

        public async Task<(string Latitude, string Longitude)> GetCoordinatesAsync(string rawAddress)
        {
            if (string.IsNullOrWhiteSpace(rawAddress))
                return (string.Empty, string.Empty);

            string simplifiedAddress = rawAddress
            .Replace("No:", "", StringComparison.OrdinalIgnoreCase)
            .Replace("Plot No:", "", StringComparison.OrdinalIgnoreCase)
            .Trim();

            string encodedAddress = Uri.EscapeDataString(simplifiedAddress);
            string url = $"{BaseUrl}?q={encodedAddress}&format=json&limit=1";

            using var client = new HttpClient();
            client.DefaultRequestHeaders.Add("User-Agent", UserAgent);

            try
            {
                string response = await client.GetStringAsync(url);

                var results = JsonSerializer.Deserialize<LocationResult[]>(response);

                if (results != null && results.Length > 0)
                {
                    return (results[0].lat, results[0].lon);
                }
            }
            catch (HttpRequestException ex)
            {
                Console.WriteLine($"HTTP Request failed: {ex.Message}");
            }
            catch (JsonException ex)
            {
                Console.WriteLine($"JSON Parsing failed: {ex.Message}");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Unexpected error occurred: {ex.Message}");
            }

            return (null, null);
        }

        private class LocationResult
        {
            public string lat { get; set; }
            public string lon { get; set; }
        }
    }
}

