using System;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace SmartAlerts.API.Utilities
{

    public class DateOnlyJsonConverter : JsonConverter<DateOnly>
    {
        private readonly string _format = "yyyy-MM-dd";

        public override DateOnly Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
        {
            var value = reader.GetString();
            if (DateOnly.TryParseExact(value, _format, out var date))
                return date;

            throw new JsonException($"Invalid date format. Use {_format}");
        }

        public override void Write(Utf8JsonWriter writer, DateOnly value, JsonSerializerOptions options)
        {
            writer.WriteStringValue(value.ToString(_format));
        }
    }

}
