#!/usr/bin/env -S dotnet --

using System.Text.Json;
using System.Text.Json.Nodes;

if (args is not [string reportsPath] || !Directory.Exists(reportsPath))
{
    Console.WriteLine("You must specify the path to 'generated/reports' folder, created after running 'java -DbundlerMainClass=net.minecraft.data.Main -jar server.jar --reports' on the vanilla java server.");
    return 1;
}

Console.WriteLine("Make sure you have commited all changes, the script will modify registry files in-place");
Console.WriteLine("Press any key to continue...");
Console.ReadKey(true);

var registryPath = Path.GetFullPath(Path.Combine("main", "resources", "registry"));
var blocksJavaPath = Path.Combine(registryPath, "blocks_java.json");
var itemsJavaPath = Path.Combine(registryPath, "items_java.json");

string blocksReportPath = Path.Combine(reportsPath, "blocks.json");
string registriesReportPath = Path.Combine(reportsPath, "registries.json");

if (!File.Exists(blocksJavaPath) || !File.Exists(itemsJavaPath))
{
    Console.WriteLine("Error: Could not find 'blocks_java.json' or 'items_java.json' in registry.");
    return 1;
}

if (!File.Exists(blocksReportPath) || !File.Exists(registriesReportPath))
{
    Console.WriteLine("Error: Could not find report files.");
    return 1;
}

Console.WriteLine("Updating Java Block mappings...");
UpdateBlockMappings(blocksJavaPath, blocksReportPath);

Console.WriteLine("Updating Java Item mappings...");
UpdateItemMappings(itemsJavaPath, registriesReportPath);

Console.WriteLine("Processing complete! Updated files written to 'blocks_java_updated.json' and 'items_java_updated.json'.");

return 0;

static void UpdateBlockMappings(string blocksPath, string reportBlocksPath)
{
    var stateToIdMap = new Dictionary<string, int>();
    var reportNode = JsonNode.Parse(File.ReadAllText(reportBlocksPath))?.AsObject();

    if (reportNode is not null)
    {
        foreach (var (blockName, blockValue) in reportNode)
        {
            var states = blockValue?["states"]?.AsArray();
            if (states is null)
            {
                continue;
            }

            foreach (var state in states)
            {
                var stateId = state!["id"]!.GetValue<int>();
                var properties = state["properties"]?.AsObject();

                string fullStateName;
                if (properties != null && properties.Count > 0)
                {
                    var propStrings = properties
                        .OrderBy(p => p.Key)
                        .Select(p => $"{p.Key}={p.Value!.GetValue<string>()}");

                    fullStateName = $"{blockName}[{string.Join(",", propStrings)}]";
                }
                else
                {
                    fullStateName = blockName;
                }

                stateToIdMap[fullStateName] = stateId;
            }
        }
    }

    var jsonOptions = new JsonSerializerOptions { WriteIndented = true };
    var oldBlocksArray = JsonNode.Parse(File.ReadAllText(blocksPath))?.AsArray() ?? new JsonArray();

    var existingNames = new HashSet<string>();
    var updated = 0;

    foreach (var entry in oldBlocksArray)
    {
        var name = entry!["name"]?.GetValue<string>();

        if (name is not null)
        {
            existingNames.Add(name);

            if (stateToIdMap.TryGetValue(name, out int newId))
            {
                entry["id"] = newId;
                updated++;
            }
        }
    }

    var newlyAdded = 0;
    foreach (var (stateName, stateId) in stateToIdMap)
    {
        if (!existingNames.Contains(stateName))
        {
            var newEntry = new JsonObject
            {
                ["name"] = stateName,
                ["id"] = stateId,
                ["bedrock"] = new JsonObject
                {
                    ["ignore"] = true
                }
            };

            oldBlocksArray.Add(newEntry);
            newlyAdded++;
        }
    }

    var sortedNodes = oldBlocksArray
        .OrderBy(node => node!["name"]?.GetValue<string>(), StringComparer.Ordinal)
        .Select(node => node!.DeepClone())
        .ToArray();

    File.WriteAllText(blocksPath, new JsonArray(sortedNodes).ToJsonString(jsonOptions));
    Console.WriteLine($"  -> Blocks updated: {updated}");
    Console.WriteLine($"  -> New blockstates appended (ignored): {newlyAdded}");
}

static void UpdateItemMappings(string itemsPath, string reportRegistriesPath)
{
    var itemToIdMap = new Dictionary<string, int>();
    var registriesNode = JsonNode.Parse(File.ReadAllText(reportRegistriesPath));
    var itemEntries = registriesNode?["minecraft:item"]?["entries"]?.AsObject();

    if (itemEntries is not null)
    {
        foreach (var (itemName, itemValue) in itemEntries)
        {
            var protocolId = itemValue!["protocol_id"]!.GetValue<int>();
            itemToIdMap[itemName] = protocolId;
        }
    }

    var jsonOptions = new JsonSerializerOptions { WriteIndented = true };
    var oldItemsArray = JsonNode.Parse(File.ReadAllText(itemsPath))?.AsArray() ?? new JsonArray();

    var existingItems = new HashSet<string>();
    var updated = 0;

    foreach (var entry in oldItemsArray)
    {
        var name = entry!["name"]?.GetValue<string>();
        if (name is not null)
        {
            existingItems.Add(name);

            if (itemToIdMap.TryGetValue(name, out int newId))
            {
                entry["id"] = newId;
                updated++;
            }
        }
    }

    var newlyAdded = 0;
    foreach (var (itemName, itemId) in itemToIdMap)
    {
        if (!existingItems.Contains(itemName))
        {
            var newEntry = new JsonObject
            {
                ["name"] = itemName,
                ["id"] = itemId,
                ["bedrock"] = new JsonObject
                {
                    ["ignore"] = true
                }
            };

            oldItemsArray.Add(newEntry);
            newlyAdded++;
        }
    }

    var sortedNodes = oldItemsArray
        .OrderBy(node => node!["name"]?.GetValue<string>(), StringComparer.Ordinal)
        .Select(node => node!.DeepClone())
        .ToArray();

    File.WriteAllText(itemsPath, new JsonArray(sortedNodes).ToJsonString(jsonOptions));
    Console.WriteLine($"  -> Items updated: {updated}");
    Console.WriteLine($"  -> New items appended (ignored): {newlyAdded}");
}
