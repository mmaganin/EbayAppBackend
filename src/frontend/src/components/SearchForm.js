import React, { useState, useEffect } from 'react'
import {
    Card, TextField, FormControl, InputLabel, Select, MenuItem, FormGroup, FormControlLabel, Checkbox, Button, Box, TextareaAutosize
} from '@mui/material';

const cardStyle = {
    margin: 2,
    padding: 2,
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    maxWidth: '40rem'
}
const sortTypeStyle = {
    minWidth: '10rem',
    margin: 1
}
const checksContainerStyle = {
    display: 'flex',
    margin: 2
}
const textFieldStyle = {
    margin: 1
}
const jsonTextFieldStyle = {
    width: '20rem',
    margin: '1rem'
}

const SearchForm = (props) => {
    const { formTitle, status, fetchFrom } = props
    const { canRun, checkedListingsSize } = status;

    const [keyword, setKeyword] = useState("")
    const [numberOfResults, setNumberOfResults] = useState("50")
    const [lowPrice, setLowPrice] = useState("")
    const [highPrice, setHighPrice] = useState("")
    const [sortType, setSortType] = useState("")
    const [jsonField, setJsonField] = useState("")

    const [buyingOptions, setBuyingOptions] = useState("")
    const [conditions, setConditions] = useState("")

    const [newConditionChecked, setNewConditionChecked] = useState(false)
    const [usedConditionChecked, setUsedConditionChecked] = useState(false)
    const [unspecifiedConditionChecked, setUnspecifiedConditionChecked] = useState(false)

    const [fixedPriceChecked, setFixedPriceChecked] = useState(false)
    const [auctionChecked, setAuctionChecked] = useState(false)
    const [bestOfferChecked, setBestOfferChecked] = useState(false)

    useEffect(() => {
        generateConditionsStr()
    }, [newConditionChecked, usedConditionChecked, unspecifiedConditionChecked]);
    useEffect(() => {
        generateBuyingOptionsStr()
    }, [fixedPriceChecked, auctionChecked, bestOfferChecked]);

    function changeKeyword(e) {
        setKeyword(e.target.value)
    }
    function changeNumResults(e) {
        setNumberOfResults(e.target.value)
    }
    function changeLowPrice(e) {
        setLowPrice(e.target.value)
    }
    function changeHighPrice(e) {
        setHighPrice(e.target.value)
    }
    function changeSortType(e) {
        setSortType(e.target.value)
    }
    function changeJsonField(e) {
        setJsonField(e.target.value)
    }

    function changeNewConditionChecked(e) {
        setNewConditionChecked(e.target.checked)
    }
    function changeUsedConditionChecked(e) {
        setUsedConditionChecked(e.target.checked)
    }
    function changeUnspecifiedConditionChecked(e) {
        setUnspecifiedConditionChecked(e.target.checked)
    }

    function changeFixedPriceChecked(e) {
        setFixedPriceChecked(e.target.checked)
    }
    function changeAuctionChecked(e) {
        setAuctionChecked(e.target.checked)
    }
    function changeBestOfferChecked(e) {
        setBestOfferChecked(e.target.checked)
    }

    function getTextField(props) {
        const { id, label, onChange } = props
        return (
            <TextField sx={textFieldStyle}
                variant="outlined"
                id={id}
                label={label}
                onChange={onChange}
            />
        )
    }
    function getFormControlLabel(props) {
        const { checked, onChange, label } = props
        return (
            <FormControlLabel
                control={
                    <Checkbox
                        checked={checked}
                        onChange={onChange}
                    />}
                label={label} />
        )
    }

    function generateConditionsStr() {
        var conditionsStr = "";

        if (newConditionChecked) {
            conditionsStr += "NEW"
        }
        if (usedConditionChecked) {
            if (conditionsStr !== "") conditionsStr += "|";
            conditionsStr += "USED"
        }
        if (unspecifiedConditionChecked) {
            if (conditionsStr !== "") conditionsStr += "|";
            conditionsStr += "UNSPECIFIED"
        }

        setConditions(conditionsStr)
    }
    function generateBuyingOptionsStr() {
        var buyingOptionsStr = "";

        if (fixedPriceChecked) {
            buyingOptionsStr += "FIXED_PRICE"
        }
        if (bestOfferChecked) {
            if (buyingOptionsStr !== "") buyingOptionsStr += "|";
            buyingOptionsStr += "BEST_OFFER"
        }
        if (auctionChecked) {
            if (buyingOptionsStr !== "") buyingOptionsStr += "|";
            buyingOptionsStr += "AUCTION"
        }

        setBuyingOptions(buyingOptionsStr)
    }
    function submitSearch() {
        var json
        if (jsonField.trim() === "") {
            if (
                //checks if required fields are empty
                keyword.trim() === ""
                || (!newConditionChecked && !usedConditionChecked && !unspecifiedConditionChecked)
                || (!auctionChecked && !bestOfferChecked && !fixedPriceChecked)
                //checks that the prices and number of results are numeric
                || numberOfResults.trim() === ""
                || isNaN(numberOfResults)
                || (lowPrice.trim() !== "" && isNaN(lowPrice))
                || (highPrice.trim() !== "" && isNaN(highPrice))
                //checks that low price is less than high price
                || (lowPrice.trim() !== "" && highPrice.trim() !== "" && parseFloat(lowPrice) >= parseFloat(highPrice))
            ) {
                window.alert("Please enter a keyword, number of results, conditions, and buying options. Number of results, and low and high price must be valid numbers. Sort type and low and high price can be left empty. Sort type is Best Match when left empty")
                return;
            }
            json = JSON.stringify({
                keyword: keyword,
                lowPrice: lowPrice,
                highPrice: highPrice,
                numberOfResults: numberOfResults,
                conditions: conditions,
                sortType: sortType,
                buyingOptions: buyingOptions,
            })
        } else {
            json = jsonField
        }

        var apiCallLoad = {
            fetchFrom: fetchFrom,
            payload: {
                method: 'POST',
                headers: {
                    "Content-Type": "application/json",
                },
                body: json
            }

        }
        console.log(json)
        fetch(apiCallLoad.fetchFrom, apiCallLoad.payload)
        window.location.reload()
    }

    return (
        <Card sx={cardStyle}>
            <Box>{formTitle}</Box>
            {canRun && checkedListingsSize === 0
                ?
                <>
                    <Box>
                        {getTextField({ id: "keyword-field", label: "Enter Keyword", onChange: (e) => changeKeyword(e) })}
                        {getTextField({ id: "num-results-field", label: "Enter # Results", onChange: (e) => changeNumResults(e) })}
                    </Box>
                    <Box>
                        {getTextField({ id: "low-price-field", label: "Enter Low Price", onChange: (e) => changeLowPrice(e) })}
                        {getTextField({ id: "high-price-field", label: "Enter High Price", onChange: (e) => changeHighPrice(e) })}
                    </Box>
                    <FormControl sx={sortTypeStyle}>
                        <InputLabel id="sort-type-select-label">Sort Type</InputLabel>
                        <Select
                            labelId="sort-type-select-label"
                            id="sort-type-select"
                            value={sortType}
                            label="Sort Type"
                            onChange={changeSortType}
                        >
                            <MenuItem value={"newlyListed"}>Newly Listed</MenuItem>
                            <MenuItem value={""}>Best Match</MenuItem>
                            <MenuItem value={"endingSoonest"}>Ending Soonest</MenuItem>
                            <MenuItem value={"-price"}>Price Descending</MenuItem>
                            <MenuItem value={"price"}>Price Ascending</MenuItem>
                        </Select>
                    </FormControl>
                    <Box sx={checksContainerStyle}>
                        <FormGroup>
                            Conditions
                            {getFormControlLabel({ checked: newConditionChecked, onChange: (e) => changeNewConditionChecked(e), label: "New" })}
                            {getFormControlLabel({ checked: usedConditionChecked, onChange: (e) => changeUsedConditionChecked(e), label: "Used" })}
                            {getFormControlLabel({ checked: unspecifiedConditionChecked, onChange: (e) => changeUnspecifiedConditionChecked(e), label: "Unspecified" })}
                        </FormGroup>
                        <FormGroup>
                            Buying Options
                            {getFormControlLabel({ checked: fixedPriceChecked, onChange: (e) => changeFixedPriceChecked(e), label: "Fixed Price" })}
                            {getFormControlLabel({ checked: bestOfferChecked, onChange: (e) => changeBestOfferChecked(e), label: "Best Offer" })}
                            {getFormControlLabel({ checked: auctionChecked, onChange: (e) => changeAuctionChecked(e), label: "Auction" })}
                        </FormGroup>
                    </Box>
                    OR
                    <TextareaAutosize
                        placeholder="Enter JSON Text Here. Make sure it is correct."
                        style={jsonTextFieldStyle}
                        minRows={3}
                        onChange={changeJsonField}
                    />
                    <Button
                        variant="outlined"
                        onClick={submitSearch}
                    >Submit Search</Button>
                </>
                :
                <Box>
                    This Thread is running, refresh page after resetting to check if it is ready again
                    <Button
                        variant="outlined"
                        onClick={() => handleStopThread(fetchFrom)}
                    >Reset Thread</Button>
                </Box>
            }
        </Card>
    );
}

function handleStopThread(fetchFrom) {
    var apiCallLoad = {
        fetchFrom: fetchFrom,
        payload: {
            method: 'PUT',
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
            window.alert("Stop Thread Success! Please wait for thread to be useable again.")
            console.log(ebayStatus)
            window.location.reload()
        })
        .catch((error) => {
            window.alert("Stop Thread Failed")
            console.log("Stop Thread Failed: " + error)
            window.location.reload()
        })
}

export default SearchForm;