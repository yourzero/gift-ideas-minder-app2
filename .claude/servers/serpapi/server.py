#!/usr/bin/env python3
# .claude/servers/serpapi/server.py
import os
from typing import Optional, Any, Dict

from mcp.server.fastmcp import FastMCP
from serpapi import GoogleSearch

mcp = FastMCP("serpapi")

def _require_key() -> str:
    key = os.environ.get("SERPAPI_API_KEY")
    if not key:
        raise RuntimeError("SERPAPI_API_KEY is not set")
    return key

@mcp.tool()
def search(
    query: str,
    engine: str = "google",
    location: Optional[str] = None,
    num: Optional[int] = 10,
    **extra: Any
) -> Dict[str, Any]:
    """Run a SerpAPI search across engines (google, bing, youtube, ebay, walmart, ...)."""
    key = _require_key()
    params = {"api_key": key, "q": query, "engine": engine}
    if location:
        params["location"] = location
    if num is not None:
        params["num"] = num
    params.update(extra)
    return {
        "engine": engine,
        "query": query,
        "location": location,
        "params": params,
        "results": GoogleSearch(params).get_dict(),
    }

@mcp.tool()
def locations(q: str, limit: int = 10) -> Dict[str, Any]:
    """Look up canonical Google location strings (e.g., 'Indianapolis, Indiana')."""
    key = _require_key()
    locs = GoogleSearch({"api_key": key}).get_location(q, limit)
    return {"query": q, "locations": locs}

if __name__ == "__main__":
    # Default transport is stdio; perfect for your .mcp.json entry.
    mcp.run()
