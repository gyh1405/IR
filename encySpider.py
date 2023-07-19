import datetime
from urllib.parse import urljoin
import scrapy


class EncySpider(scrapy.Spider):
    name = 'encyspider'
    allowed_domains = ['www.encyclopedia.com']
    start_urls = ['https://www.encyclopedia.com/women/encyclopedias-almanacs-transcripts-and-maps/glasgow-ellen-1873-1945']
    # custom_settings = {
    #     'DUPEFILTER_CLASS': 'scrapy.dupefilters.BaseDupeFilter',
    # }
    url_frontier = set()  # Store unique URLs

    def start_requests(self):
        for url in self.start_urls:
            yield scrapy.Request(url, callback=self.parse)

    def parse(self, response):

        # Getting the box that contains all the info we want
        result_container = response.xpath('//div[@id ="topic_wrap"]')

        current_url = response.url

        # Looping through each result listed in the resultt_container box
        for result in result_container:
            result_title = result.xpath('.//h1[@class="doctitle"]/text()').get()
            result_content = result.xpath('.//div[@class="doccontentwrapper collapse show"]/p/text()').getall()
            result_urls = result.xpath('.//a/@href').getall() #Put into URL frontier, seperate list or doc, can check whether if url alr exist in doc
            result_timestamp = datetime.datetime.now()

            # Return data extracted
            yield {
                'title':result_title,
                'content':result_content,
                'datetime':result_timestamp,
                'url': current_url
            }
            if result_urls is not None:
                for next_page in result_urls:
                    absolute_url = urljoin(response.url, next_page)  # Convert relative URL to absolute URL
                    if absolute_url not in self.url_frontier:  # Check if URL is already visited
                        self.url_frontier.add(absolute_url)  # Add URL to visited set
                        yield response.follow(next_page, self.parse)
                        # Write absolute URL to a text file
                        with open('url_frontier.txt', 'a') as f:
                            f.write(absolute_url + '\n')

        # for result in result_container:
        #     next_pages = result.xpath('.//a/@href').getall()
        #     if next_pages is not None:
        #         for next_page in next_pages:
        #             yield response.follow(next_page, self.parse)
