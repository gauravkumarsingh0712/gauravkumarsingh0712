
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProductDetail {

        @Expose
        private Integer id;
        @Expose
        private String title;
        @Expose
        private String description;
        @Expose
        private String specification;
        @Expose
        private String type;
        @Expose
        private Integer quantity;
        @SerializedName("hex_symbol")
        @Expose
        private String hexSymbol;
        @Expose
        private Double price;
        @SerializedName("discount_price")
        @Expose
        private Double discountPrice;
        @SerializedName("discount_percent")
        @Expose
        private Integer discountPercent;
        @Expose
        private String state;
        @SerializedName("stitching_available")
        @Expose
        private Boolean stitchingAvailable;
        @Expose
        private Designer designer;
        @Expose
        private List<Category> categories = new ArrayList<Category>();
        @Expose
        private ArrayList<Image> images = new ArrayList<Image>();
        @Expose
        private List<Variant> variants = new ArrayList<Variant>();
        @SerializedName("addon_types")
        @Expose
        private List<AddonType> addonTypes = new ArrayList<AddonType>();
        @Expose
        private Specifications specifications;

        /**
         *
         * @return
         * The id
         */
        public Integer getId() {
                return id;
        }

        /**
         *
         * @param id
         * The id
         */
        public void setId(Integer id) {
                this.id = id;
        }

        /**
         *
         * @return
         * The title
         */
        public String getTitle() {
                return title;
        }

        /**
         *
         * @param title
         * The title
         */
        public void setTitle(String title) {
                this.title = title;
        }

        /**
         *
         * @return
         * The description
         */
        public String getDescription() {
                return description;
        }

        /**
         *
         * @param description
         * The description
         */
        public void setDescription(String description) {
                this.description = description;
        }

        /**
         *
         * @return
         * The specification
         */
        public String getSpecification() {
                return specification;
        }

        /**
         *
         * @param specification
         * The specification
         */
        public void setSpecification(String specification) {
                this.specification = specification;
        }

        /**
         *
         * @return
         * The type
         */
        public String getType() {
                return type;
        }

        /**
         *
         * @param type
         * The type
         */
        public void setType(String type) {
                this.type = type;
        }

        /**
         *
         * @return
         * The quantity
         */
        public Integer getQuantity() {
                return quantity;
        }

        /**
         *
         * @param quantity
         * The quantity
         */
        public void setQuantity(Integer quantity) {
                this.quantity = quantity;
        }

        /**
         *
         * @return
         * The hexSymbol
         */
        public String getHexSymbol() {
                return hexSymbol;
        }

        /**
         *
         * @param hexSymbol
         * The hex_symbol
         */
        public void setHexSymbol(String hexSymbol) {
                this.hexSymbol = hexSymbol;
        }

        /**
         *
         * @return
         * The price
         */
        public Double getPrice() {
                return price;
        }

        /**
         *
         * @param price
         * The price
         */
        public void setPrice(Double price) {
                this.price = price;
        }

        /**
         *
         * @return
         * The discountPrice
         */
        public Double getDiscountPrice() {
                return discountPrice;
        }

        /**
         *
         * @param discountPrice
         * The discount_price
         */
        public void setDiscountPrice(Double discountPrice) {
                this.discountPrice = discountPrice;
        }

        /**
         *
         * @return
         * The discountPercent
         */
        public Integer getDiscountPercent() {
                return discountPercent;
        }

        /**
         *
         * @param discountPercent
         * The discount_percent
         */
        public void setDiscountPercent(Integer discountPercent) {
                this.discountPercent = discountPercent;
        }

        /**
         *
         * @return
         * The state
         */
        public String getState() {
                return state;
        }

        /**
         *
         * @param state
         * The state
         */
        public void setState(String state) {
                this.state = state;
        }

        /**
         *
         * @return
         * The stitchingAvailable
         */
        public Boolean getStitchingAvailable() {
                return stitchingAvailable;
        }

        /**
         *
         * @param stitchingAvailable
         * The stitching_available
         */
        public void setStitchingAvailable(Boolean stitchingAvailable) {
                this.stitchingAvailable = stitchingAvailable;
        }

        /**
         *
         * @return
         * The designer
         */
        public Designer getDesigner() {
                return designer;
        }

        /**
         *
         * @param designer
         * The designer
         */
        public void setDesigner(Designer designer) {
                this.designer = designer;
        }

        /**
         *
         * @return
         * The categories
         */
        public List<Category> getCategories() {
                return categories;
        }

        /**
         *
         * @param categories
         * The categories
         */
        public void setCategories(List<Category> categories) {
                this.categories = categories;
        }

        /**
         *
         * @return
         * The images
         */
        public ArrayList<Image> getImages() {
                return images;
        }

        /**
         *
         * @param images
         * The images
         */
        public void setImages(ArrayList<Image> images) {
                this.images = images;
        }

        /**
         *
         * @return
         * The variants
         */
        public List<Variant> getVariants() {
                return variants;
        }

        /**
         *
         * @param variants
         * The variants
         */
        public void setVariants(List<Variant> variants) {
                this.variants = variants;
        }

        /**
         *
         * @return
         * The addonTypes
         */
        public List<AddonType> getAddonTypes() {
                return addonTypes;
        }

        /**
         *
         * @param addonTypes
         * The addon_types
         */
        public void setAddonTypes(List<AddonType> addonTypes) {
                this.addonTypes = addonTypes;
        }

        /**
         *
         * @return
         * The specifications
         */
        public Specifications getSpecifications() {
                return specifications;
        }

        /**
         *
         * @param specifications
         * The specifications
         */
        public void setSpecifications(Specifications specifications) {
                this.specifications = specifications;
        }

}