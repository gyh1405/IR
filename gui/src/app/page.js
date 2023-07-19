'use client'

// import React, { useState } from 'react';
// // import Cors from 'cors';

// const SearchEngine = ({ solrApiUrl }) => {
//   // const cors = Cors();

//   const [searchResults, setSearchResults] = useState([]);
//   const [searchInput, setSearchInput] = useState('');

//   const handleSearch = async (event) => {
//     try {
//       const value = event.target.value;
//       setSearchInput(value);
     
      
//       const url = `${solrApiUrl}/search?query=${encodeURIComponent(value)}`;
//       const response = await fetch(url, {
//         method: 'GET',
//         mode: 'cors',
//         headers: {
//           'Content-Type': 'application/json',
//         },
//       });

//       if (response.ok) {
//         // Get response data
//         const data = await response.json();
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
//     <div className='flex flex-col items-center justify-center min-h-screen bg-sky-50'>
//       <h1 className='text-4xl mb-8'>Search Engine GUI</h1>
//       <input
//         className='mb-8 px-3 py-2 w-1/2 rounded-lg shadow-md focus:outline-none focus:ring-2 focus:ring-blue-600 focus:border-transparent'
//         placeholder="Search..."
//         value={searchInput}
//         onChange={handleSearch}
//       />
//       {searchResults.length > 0 ? (
//         <div className='w-1/2'>
//           {searchResults.map((item) => (
//             <div key={item.id} className='mb-4 p-3 shadow-lg rounded-lg bg-white'>

//               <h3 className='text-2xl mb-2'>{item}</h3>
//               {/* <h3 className='text-2xl mb-2'>{item.name}</h3>
//               <p className='text-lg'>{item.author}</p> */}
//             </div>
//           ))}
//         </div>
//       ) : (
//         <p className='text-xl'>No results found.</p>
//       )}
//     </div>
//   );
// };

// export default function Home() {
//   return <SearchEngine solrApiUrl="//localhost:8080/api" />;
// }

import React, { useState } from 'react';

const SearchEngine = ({ solrApiUrl }) => {
  const [searchResults, setSearchResults] = useState([]);
  const [searchInput, setSearchInput] = useState('');

  const handleSearch = async (event) => {
    try {
      const value = event.target.value;
      setSearchInput(value);
      
      const url = `${solrApiUrl}/search?query=${encodeURIComponent(value)}`;
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        // Get response data
        const data = await response.json();
        console.log(data);
        // Set search results
        setSearchResults(data);
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
          {searchResults.map((item, i) => (
            console.log(item),
            <div key={i} className='mb-4 p-3 shadow-lg rounded-lg bg-white'>
              <h3 className='text-2xl mb-2'>{item["fields"][0]["charSequenceValue"]}</h3>
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
  return <SearchEngine solrApiUrl="http://localhost:8080/api" />;
}
