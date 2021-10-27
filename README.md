# Java core for Verifiable Credentials

This library provides the functionality for signing and verifying Verifiable Presentations.

## Supported verification methods

The only supported verification method is "Canonical JSON with JWS". This is not a W3C standardised method. The document is canonicalized as JSON using the 
SETL Canonical JSON specification. A detached JSON Web Signature is then calculated using the appropriate algorithm for the signing key.

No W3C standardised method is supported as they are all based upon the "URDNA2015" algorithm. This algorithm requires an NP-complete algorithm. Documents of 
only a few hundred bytes cannot be processed within the remaining lifetime of the universe. This makes it trivially easy to instigate a denial of service 
attack by submitting documents for signature verification.

Additionally, the URDNA2015 algorithm signs the Resource Description Framework representation of the verifiable credential, not the JSON-LD representation. 
This means it is possible to change any text value in the JSON, rename any field in the JSON, and add any field to the JSON without breaking the signature 
by suitably amending the JSON-LD context, which is not protected by the document signature.

By contrast, the "Canonical JSON with JWS" protects all parts of the JSON-LD and the RDF representation and operates in linear time.

