
'use client'
// import React, { useState } from 'react';

// const SearchEngine = ({ solrApiUrl }) => {

//   const [searchResults, setSearchResults] = useState([]);

//   const handleSearch = async (event) => {
//     try {
//       const value = event.target.value;

     
//       const queryValue = `*${value}*`; // Use wildcard (*) to match any content containing the search value
//       const url = `${solrApiUrl}/select?q=${encodeURIComponent(queryValue)}`;
//       const response = await fetch(url, {
//         method: 'GET',
//         mode: 'cors',
//         headers: {
//           'Content-Type': 'application/json',
//         },
//       });

//       ///select?q=   name%3Alightning
      
//       if (response.ok) {
//         // Get response data
//         const data = await response.json();
//         //console.log(data);
        
//         // Set search results
//         setSearchResults(data.response.docs);
//       } else {
//         console.error('Search request failed.');
//       }
//     } catch (error) {
//       console.error('Error occurred while searching:', error);
//     }
//   };

//   return (
//     <div className='bg-sky-50 '>
//       <h1>Search Engine GUI</h1>
//       <input
//         className='shadow-md rounded-xl h-10 pl-2 w-1/2 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent'
//         placeholder="Search..."
//         onChange={handleSearch}
//       />
//       {searchResults.length > 0 ? (
//         <ul>
         
//           {searchResults.map((item) => (

//             console.log(item),
//             <li key={item.id}>
//               <h3>{item.name}</h3>
//               <p>{item.author}</p>
//             </li>
//           ))}
//         </ul>
//       ) : (
//         <p>No results found.</p>
//       )}
//     </div>
//   );
// };


// export default function Home() {
//   return (
//     <>
//     {<SearchEngine solrApiUrl="//localhost:8983/solr/techproducts" />}
//     </>
//   )
// }

import React, { useState } from 'react';

const SearchEngine = ({ solrApiUrl }) => {
  const [searchResults, setSearchResults] = useState([]);
  const [searchInput, setSearchInput] = useState('');

  const handleSearch = async (event) => {
    try {
      const value = event.target.value;
      setSearchInput(value);
     
      const queryValue = `*${value}*`; // Use wildcard (*) to match any content containing the search value
      const url = `${solrApiUrl}/select?q=${encodeURIComponent(queryValue)}`;
      const response = await fetch(url, {
        method: 'GET',
        mode: 'cors',
        headers: {
          'Content-Type': 'application/json',
        },
      });

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
    <div className='flex flex-col items-center justify-center min-h-screen bg-sky-50'>
      <h1 className='text-4xl mb-8'>Search Engine GUI</h1>
      <input
        className='mb-8 px-3 py-2 w-1/2 rounded-lg shadow-md focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent'
        placeholder="Search..."
        value={searchInput}
        onChange={handleSearch}
      />
      {searchResults.length > 0 ? (
        <div className='w-1/2'>
          {searchResults.map((item) => (
            <div key={item.id} className='mb-4 p-3 shadow-lg rounded-lg bg-white'>
              <h3 className='text-2xl mb-2'>{item.name}</h3>
              <p className='text-lg'>{item.author}</p>
            </div>
          ))}
        </div>
      ) : (
        <p className='text-xl'>No results found.</p>
      )}
    </div>
  );
};

export default function Home() {
  return <SearchEngine solrApiUrl="//localhost:8983/solr/techproducts" />;
}
