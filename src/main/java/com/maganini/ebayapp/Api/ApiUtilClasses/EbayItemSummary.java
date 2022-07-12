package com.maganini.ebayapp.Api.ApiUtilClasses;

import lombok.Data;

import java.sql.Date;
import java.util.ArrayList;

@Data
public class EbayItemSummary {
    public ArrayList<AdditionalImage> additionalImages;
    public String adultOnly;
    public String availableCoupons;
    public String bidCount;
    public ArrayList<String> buyingOptions;
    public ArrayList<Category> categories;
    public String compatibilityMatch;
    public ArrayList<CompatibilityProperty> compatibilityProperties;
    public String condition;
    public String conditionId;
    public CurrentBidPrice currentBidPrice;
    public DistanceFromPickupLocation distanceFromPickupLocation;
    public String energyEfficiencyClass;
    public String epid;
    public Image image;
    public String itemAffiliateWebUrl;
    public Date itemCreationDate;
    public Date itemEndDate;
    public String itemGroupHref;
    public String itemGroupType;
    public String itemHref;
    public String itemId;
    public ItemLocation itemLocation;
    public String itemWebUrl;
    public String legacyItemId;
    public String listingMarketplaceId;
    public MarketingPrice marketingPrice;
    public ArrayList<PickupOption> pickupOptions;
    public EbayPrice price;
    public String priceDisplayCondition;
    public String priorityListing;
    public ArrayList<String> qualifiedPrograms;
    public Seller seller;
    public ArrayList<ShippingOption> shippingOptions;
    public String shortDescription;
    public ArrayList<ThumbnailImage> thumbnailImages;
    public String title;
    public String topRatedBuyingExperience;
    public String tyreLabelImageUrl;
    public UnitPrice unitPrice;
    public String unitPricingMeasure;
    public String watchCount;
}
@Data
class AdditionalImage{
    public String height;
    public String imageUrl;
    public String width;
}
@Data
class Category{
    public String categoryId;
}
@Data
class CompatibilityProperty{
    public String localizedName;
    public String name;
    public String value;
}
@Data
class CurrentBidPrice{
    public String convertedFromCurrency;
    public String convertedFromValue;
    public String currency;
    public String value;
}
@Data
class DiscountAmount{
    public String convertedFromCurrency;
    public String convertedFromValue;
    public String currency;
    public String value;
}
@Data
class DistanceFromPickupLocation{
    public String unitOfMeasure;
    public String value;
}
@Data
class Image{
    public String height;
    public String imageUrl;
    public String width;
}
@Data
class ItemLocation{
    public String addressLine1;
    public String addressLine2;
    public String city;
    public String country;
    public String county;
    public String postalCode;
    public String stateOrProvince;
}
@Data
class MarketingPrice{
    public DiscountAmount discountAmount;
    public String discountPercentage;
    public OriginalPrice originalPrice;
    public String priceTreatment;
}
@Data
class OriginalPrice{
    public String convertedFromCurrency;
    public String convertedFromValue;
    public String currency;
    public String value;
}
@Data
class PickupOption{
    public String pickupLocationType;
}

@Data
class Seller{
    public String feedbackPercentage;
    public String feedbackScore;
    public String sellerAccountType;
    public String username;
}
@Data
class ShippingCost{
    public String convertedFromCurrency;
    public String convertedFromValue;
    public String currency;
    public String value;
}
@Data
class ShippingOption{
    public String guaranteedDelivery;
    public String maxEstimatedDeliveryDate;
    public String minEstimatedDeliveryDate;
    public ShippingCost shippingCost;
    public String shippingCostType;
}
@Data
class ThumbnailImage{
    public String height;
    public String imageUrl;
    public String width;
}
@Data
class UnitPrice{
    public String convertedFromCurrency;
    public String convertedFromValue;
    public String currency;
    public String value;
}