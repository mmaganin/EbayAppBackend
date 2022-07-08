
import React, { useState, useEffect } from 'react'
import {
  Box, Card
} from '@mui/material';

import SearchForm from './SearchForm'

const ebayPageStyle = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center'
}
const titleStyle = {
  margin: 2,
  padding: 2,
  maxWidth: '90rem',
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center'
}
const threadFormsStyle = {
  display: 'grid',
  gridTemplateColumns: 'repeat(2, 1fr)'
}

const Ebay = () => {
  const [ebayStatus, setEbayStatus] = useState(null)

  useEffect(() => {
    if (ebayStatus === null) handleGetStatus();
  }, [ebayStatus]);

  function handleGetStatus() {
    var apiCallLoad = {
      fetchFrom: 'http://localhost:8080/api/ebay/status',
      payload: {
        method: 'GET',
        headers: {
          "Content-Type": "application/json",
        }
      }
    }
    fetch(apiCallLoad.fetchFrom, apiCallLoad.payload)
      .then(response => {
        if (!response.ok) throw new Error(response.status);
        else return response.json();
      })
      .then((ebayStatus) => {
        setEbayStatus(ebayStatus)
        console.log(ebayStatus)
      })
      .catch((error) => {
        window.alert("Get Ebay Status Failed")
        console.log("Get Ebay Status Failed: " + error)
      })
  }

  return (
    <Box sx={ebayPageStyle}>
      <Card sx={titleStyle}>
        <Box>Ebay Search Page</Box>
        <Box>Enter a search into one of the forms to regularly check Ebay for new listings that match the given search.</Box>
        <Box>Fast threads check Ebay for a given search every 25 seconds. Slow threads check every 50 seconds.</Box>
        <Box>Ebay allows for 5000 calls every day per every Ebay dev account. Only one fast thread should be used per account as it reaches close to 5000 calls for 24 hours. Two slow threads can be used per account.</Box>
        <Box>Sample JSON to use for quick input:</Box>
        <Box>{'{"keyword":"airpods pro","lowPrice":"30","highPrice":"125","numberOfResults":"100","conditions":"NEW","sortType":"newlyListed","buyingOptions":"FIXED_PRICE|BEST_OFFER"}'}</Box>
      </Card>
      {ebayStatus === null ? "" :
        <Box sx={threadFormsStyle}>
          <SearchForm formTitle='Slow Thread 1' fetchFrom='http://localhost:8080/api/ebay/slow1' status={{ canRun: ebayStatus.canRunSlow1, checkedListingsSize: ebayStatus.checkedListingsSlow1Size }} />
          <SearchForm formTitle='Slow Thread 2' fetchFrom='http://localhost:8080/api/ebay/slow2' status={{ canRun: ebayStatus.canRunSlow2, checkedListingsSize: ebayStatus.checkedListingsSlow2Size }} />
          <SearchForm formTitle='Fast Thread 1' fetchFrom='http://localhost:8080/api/ebay/fast1' status={{ canRun: ebayStatus.canRunFast1, checkedListingsSize: ebayStatus.checkedListingsFast1Size }} />
          <SearchForm formTitle='Fast Thread 2' fetchFrom='http://localhost:8080/api/ebay/fast2' status={{ canRun: ebayStatus.canRunFast2, checkedListingsSize: ebayStatus.checkedListingsFast2Size }} />
        </Box>
      }
    </Box>
  );
}

export default Ebay;
