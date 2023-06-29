
'use client'
import React, { useState } from 'react';

const SearchEngine = ({ solrApiUrl }) => {

  const [searchResults, setSearchResults] = useState([]);

  const handleSearch = async (event) => {
    try {
      const value = event.target.value;

     
      // Apply the CORS middleware to the fetch request
      const corsURL = `${solrApiUrl}/select?q=${value}`;
      const response = await fetch(corsURL, {
        method: 'GET',
        mode: 'cors',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      ///select?q=   name%3Alightning
      
      if (response.ok) {
        // Get response data
        const data = await response.json();
        
        // Set search results
        setSearchResults(data.response.docs);
      } else {
        console.error('Search request failed.');
      }
    } catch (error) {
      console.error('Error occurred while searching:', error);
    }
  };

  return (
    <div>
      <h1>Search Engine GUI</h1>
      <input
        placeholder="Search..."
        onChange={handleSearch}
      />
      {searchResults.length > 0 ? (
        <ul>
          {searchResults.map((item) => (
            <li key={item.id}>
              <h3>{item.title}</h3>
              <p>{item.description}</p>
            </li>
          ))}
        </ul>
      ) : (
        <p>No results found.</p>
      )}
    </div>
  );
};


export default function Home() {
  return (
    <>
    {<SearchEngine solrApiUrl="http://localhost:8983/solr/techproducts" />}
    </>
  )
}

